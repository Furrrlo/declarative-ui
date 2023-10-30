package io.github.furrrlo.dui;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.*;

public interface DeclarativeComponentContext {

    default <V> State<V> useState(V value) {
        return useState(value, Objects::deepEquals);
    }

    <V> State<V> useState(V value, BiPredicate<V, V> equalityFn);

    default <V> State<V> useState(Supplier<V> value) {
        return this.<V>useState(value, Objects::deepEquals);
    }

    <V> State<V> useState(Supplier<V> value, BiPredicate<V, V> equalityFn);

    default <V> Memo<V> useMemo(IdentifiableSupplier<V> value) {
        return useMemo(value, Objects::deepEquals);
    }

    <V> Memo<V> useMemo(IdentifiableSupplier<V> value, BiPredicate<V, V> equalityFn);

    default <V extends Serializable> V useCallback(V fun) {
        // By getting directly here, we are registering the whole component
        // as a dependency of the memo, therefore if any of the value captured
        // by the callback lambda are changed, it will trigger a re-render of the
        // entire thing. This should be fine because the captured values usually are:
        // 1. Another memo/state object (not the contained value directly), which are supposed to always be the same
        // 2. Another value which could only change if the component was re-rendered anyway
        return useMemo(IdentifiableSupplier.explicit(
                () -> fun,
                Identifiables.computeDependencies(fun)
        )).get();
    }

    default <V> V useCallbackExplicit(V fun, Object dependency) {
        return useCallbackExplicit(fun, Collections.singletonList(dependency));
    }

    default <V> V useCallbackExplicit(V fun, List<Object> dependencies) {
        return useMemo(IdentifiableSupplier.explicit(() -> fun, dependencies)).get();
    }

    <V> Ref<V> useRef(Supplier<V> initialValue);

    default <V> Ref<V> useRef(V initialValue) {
        return useRef(() -> initialValue);
    }

    default <V> Ref<V> useThrowingRef(String msg) {
        return useRef(() -> {
            throw new NoSuchElementException(msg);
        });
    }

    void useLaunchedEffect(IdentifiableThrowingRunnable effect);

    void useDisposableEffect(IdentifiableConsumer<DisposableEffectScope> effect);

    void useSideEffect(Runnable effect);

    default <V> Supplier<V> produce(Supplier<V> initialValue, IdentifiableThrowingConsumer<ProduceScope<V>> producer0) {
        final State<V> state = useState(initialValue);
        final IdentifiableThrowingConsumer<ProduceScope<V>> producer = IdentifiableThrowingConsumer.explicit(producer0);
        useLaunchedEffect(IdentifiableThrowingRunnable.explicit(() -> {
            class ProduceScopeImpl implements ProduceScope<V> {
                @Nullable ThrowingRunnable onDispose;

                @Override
                public State<V> state() {
                    return state;
                }

                @Override
                public void awaitDispose(ThrowingRunnable onDispose) throws InterruptedException {
                    this.onDispose = onDispose;

                    // Wait on a brand-new condition until interrupted
                    final Lock lock = new ReentrantLock();
                    final Condition condition = lock.newCondition();

                    lock.lock();
                    try {
                        while (!Thread.currentThread().isInterrupted())
                            condition.await();
                    } finally {
                        lock.unlock();
                    }
                }
            }

            final ProduceScopeImpl scope = new ProduceScopeImpl();
            try {
                producer.accept(scope);
            } catch (Throwable t) {
                if(scope.onDispose != null) {
                    // Save and clear the interrupt status to run the onDispose function
                    // without any interruption state set, and restore it afterward
                    boolean wasInterrupted = Thread.interrupted();
                    try {
                        scope.onDispose.run();
                    } catch (Throwable innerThrowable) {
                        t.addSuppressed(new Exception("Failed to run onDispose", innerThrowable));
                    } finally {
                        if(wasInterrupted)
                            Thread.currentThread().interrupt();
                    }
                }

                throw t;
            }
        }, producer, state));
        return state;
    }

    interface DisposableEffectScope {

        void onDispose(Runnable onDispose);
    }

    interface ProduceScope<V> {

        State<V> state();

        void awaitDispose(ThrowingRunnable onDispose) throws InterruptedException;
    }
}
