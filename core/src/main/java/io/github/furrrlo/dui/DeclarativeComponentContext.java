package io.github.furrrlo.dui;

import java.io.Serializable;
import java.util.*;
import java.util.function.*;

public interface DeclarativeComponentContext<T> {

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

    void useDisposableEffect(IdentifiableConsumer<SetOnDisposeFn> effect);

    void useSideEffect(Runnable effect);

    default <V> Supplier<V> produce(Supplier<V> initialValue, IdentifiableThrowingConsumer<State<V>> producer0) {
        final State<V> state = useState(initialValue);
        final IdentifiableThrowingConsumer<State<V>> producer = IdentifiableThrowingConsumer.explicit(producer0);
        useLaunchedEffect(IdentifiableThrowingRunnable.explicit(() -> producer.accept(state), producer, state));
        return state;
    }

    @FunctionalInterface
    interface SetOnDisposeFn extends Consumer<Runnable> {
        @Override
        void accept(Runnable onDispose);
    }
}
