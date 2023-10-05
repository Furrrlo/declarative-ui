package io.github.furrrlo.dui;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface DeclarativeComponentContext<T> {

    <V> State<V> useState(V value);

    <V> State<V> useState(Supplier<V> value);

    <V> Memo<V> useMemo(IdentifiableSupplier<V> value);

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

    <V> DeclarativeComponentContext<T> inner(Function<T, V> getter, DeclarativeComponent<V> component);

    <V> DeclarativeComponentContext<T> attribute(String key, BiConsumer<T, V> setter, Supplier<V> value);

    <V, S extends DeclarativeComponentWithIdSupplier<? extends V>> DeclarativeComponentContext<T> listAttribute(
            String key,
            Class<V> type,
            ListReplacer<T, V, S> replacer,
            List<V> fn);

    <V, S extends DeclarativeComponentWithIdSupplier<? extends V>> DeclarativeComponentContext<T> listAttribute(
            String key,
            Class<V> type,
            ListRemover<T> remover,
            List<V> fn,
            ListAdder<T, V, S> adder);

    <C> DeclarativeComponentContext<T> fnAttribute(String key,
                                                   BiConsumer<T, C> setter,
                                                   DeclarativeComponentSupplier<C> fn);

    <C, S extends DeclarativeComponentWithIdSupplier<? extends C>> DeclarativeComponentContext<T> listFnAttribute(
            String key,
            ListSetter<T, C, S> setter,
            List<S> fn);

    <C, S extends DeclarativeComponentWithIdSupplier<? extends C>> DeclarativeComponentContext<T> listFnAttribute(
            String key,
            ListReplacer<T, C, S> replacer,
            List<S> fn);

    <C, S extends DeclarativeComponentWithIdSupplier<? extends C>> DeclarativeComponentContext<T> listFnAttribute(
            String key,
            ListAdder<T, C, S> adder,
            ListRemover<T> remover,
            List<S> fn);

    @FunctionalInterface
    interface ListSetter<T, C, S extends DeclarativeComponentSupplier<? extends C>> {

        void set(T parent, List<S> supplier, List<C> child);
    }

    @FunctionalInterface
    interface ListAdder<T, C, S extends DeclarativeComponentSupplier<? extends C>> {

        void add(T parent, int idx, S supplier, C child);
    }

    @FunctionalInterface
    interface ListReplacer<T, C, S extends DeclarativeComponentSupplier<? extends C>> {

        void replace(T parent, int idx, S supplier, C child);
    }

    @FunctionalInterface
    interface ListRemover<T> {

        void remover(T parent, int idx);
    }
}
