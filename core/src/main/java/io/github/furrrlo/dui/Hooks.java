package io.github.furrrlo.dui;

import org.jspecify.annotations.Nullable;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

public final class Hooks {

    private Hooks() {
    }

    static DeclarativeComponentInternalContext useInternalCtx() {
        return StatefulDeclarativeComponent.useInternalCtx();
    }

    public static DeclarativeComponentContext useContext() {
        return useInternalCtx();
    }

    public static void grantAccess(Supplier<MethodHandles.Lookup> lookup) {
        useInternalCtx().grantAccess(lookup);
    }

    public static <V> State<V> useState(V value) {
        return useState(value, Objects::deepEquals);
    }

    public static <V> State<V> useState(V value, BiPredicate<V, V> equalityFn) {
        return useInternalCtx().useState(value, equalityFn);
    }

    public static  <V> State<V> useState(Supplier<V> value) {
        return Hooks.<V>useState(value, Objects::deepEquals);
    }

    public static <V> State<V> useState(Supplier<V> value, BiPredicate<V, V> equalityFn) {
        return useInternalCtx().useState(value, equalityFn);
    }

    public static  <V> Memo<V> useMemo(IdentityFreeSupplier<V> value) {
        return useMemo(value, Objects::deepEquals);
    }

    public static <V> Memo<V> useMemo(IdentityFreeSupplier<V> value, BiPredicate<V, V> equalityFn) {
        return useInternalCtx().useMemo(value, equalityFn);
    }

    public static <V extends Serializable> V useCallback(V fun) {
        return useCallback(null, fun);
    }

    public static <V extends Serializable> V useCallback(MethodHandles.@Nullable Lookup lookup, V fun) {
        final Collection<MethodHandles.Lookup> lookups;
        if(lookup == null) {
            lookups = StatefulDeclarativeComponent.currentLookups();
        } else {
            final Set<MethodHandles.Lookup> newLookups = new LinkedHashSet<>();
            newLookups.add(lookup);
            newLookups.addAll(StatefulDeclarativeComponent.currentLookups());
            lookups = Collections.unmodifiableSet(newLookups);
        }

        // By getting directly here, we are registering the whole component
        // as a dependency of the memo, therefore if any of the value captured
        // by the callback lambda are changed, it will trigger a re-render of the
        // entire thing. This should be fine because the captured values usually are:
        // 1. Another memo/state object (not the contained value directly), which are supposed to always be the same
        // 2. Another value which could only change if the component was re-rendered anyway
        return useMemo(IdentityFreeSupplier.explicit(
                () -> fun,
                IdentityFrees.computeDependencies(lookups, fun)
        )).get();
    }

    public static <V> V useCallbackExplicit(V fun, Object dependency) {
        return useCallbackExplicit(fun, Collections.singletonList(dependency));
    }

    public static <V> V useCallbackExplicit(V fun, List<Object> dependencies) {
        return useMemo(IdentityFreeSupplier.explicit(() -> fun, dependencies)).get();
    }

    public static <V> Ref<V> useRef(Supplier<V> initialValue) {
        return useInternalCtx().useRef(initialValue);
    }

    public static <V> Ref<V> useRef(V initialValue) {
        return useRef(() -> initialValue);
    }

    public static <V> Ref<V> useThrowingRef(String msg) {
        return useRef(() -> {
            throw new NoSuchElementException(msg);
        });
    }

    public static void useLaunchedEffect(IdentityFreeThrowingRunnable effect) {
        useInternalCtx().useLaunchedEffect(effect);
    }

    public static void useDisposableEffect(IdentityFreeConsumer<DisposableEffectScope> effect) {
        useInternalCtx().useDisposableEffect(effect);
    }

    public static void useSideEffect(Runnable effect) {
        useInternalCtx().useSideEffect(effect);
    }

    public static <V> Supplier<V> produce(Supplier<V> initialValue, IdentityFreeThrowingConsumer<ProduceScope<V>> producer) {
        final State<V> state = useState(initialValue);
        useLaunchedEffect(IdentityFreeThrowingRunnable.explicit(() -> {
            class ProduceScopeImpl implements ProduceScope<V> {
                @Nullable ThrowingRunnable onDispose;

                @Override
                public State<V> state() {
                    return state;
                }

                @Override
                @SuppressWarnings("InfiniteLoopStatement")
                public void awaitDispose(ThrowingRunnable onDispose) throws InterruptedException {
                    this.onDispose = onDispose;

                    while (true) {
                        if(Thread.interrupted())
                            throw new InterruptedException();
                        LockSupport.park(onDispose);
                    }
                }
            }

            final ProduceScopeImpl scope = new ProduceScopeImpl();
            // Abuse the try-with-resources mechanism so that exception handling is done properly e.g.
            // exceptions thrown by producer#accept are not overwritten by onDispose#run in the 'finally' block
            try(AutoCloseable closeable = () -> {
                ThrowingRunnable onDispose = scope.onDispose;
                if(onDispose == null)
                    return;

                // Save and clear the interrupt status to run the onDispose function
                // without any interruption state set, and restore it afterward
                boolean wasInterrupted = Thread.interrupted();
                try {
                    onDispose.run();
                } finally {
                    if(wasInterrupted)
                        Thread.currentThread().interrupt();
                }
            }) {
                // Make javac shut up about the following warning, as it's done on purpose and can't be suppressed
                // "[try] auto-closeable resource unused is never referenced in body of corresponding try statement"
                @SuppressWarnings("unused") AutoCloseable unused = closeable;
                producer.accept(scope);
            }
        }, producer, state));
        return state;
    }

    public interface DisposableEffectScope {

        void onDispose(Runnable onDispose);
    }

    public interface ProduceScope<V> {

        State<V> state();

        void awaitDispose(ThrowingRunnable onDispose) throws InterruptedException;
    }
}
