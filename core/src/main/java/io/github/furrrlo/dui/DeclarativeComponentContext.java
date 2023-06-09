package io.github.furrrlo.dui;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public interface DeclarativeComponentContext<T> {

    <V> State<V> useState(V value);

    <V> State<V> useState(Supplier<V> value);

    default <V> V useMemo(Supplier<V> value) {
        return useMemo(value, Collections.emptyList());
    }

    default <V> V useMemo(Supplier<V> value, Object dependency) {
        return useMemo(value, Collections.singletonList(dependency));
    }

    <V> V useMemo(Supplier<V> value, List<Object> dependencies);

    default <V> V useCallback(V fun) {
        return useCallback(fun, Collections.emptyList());
    }

    default <V> V useCallback(V fun, Object dependency) {
        return useCallback(fun, Collections.singletonList(dependency));
    }

    <V> V useCallback(V fun, List<Object> dependencies);

    <V> DeclarativeComponentContext<T> attribute(String key, BiConsumer<T, V> setter, V value);

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
