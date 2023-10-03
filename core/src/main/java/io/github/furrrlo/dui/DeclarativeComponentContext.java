package io.github.furrrlo.dui;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface DeclarativeComponentContext<T> {

    <V> State<V> useState(V value);

    <V> State<V> useState(Supplier<V> value);

    <V> V useMemo(IdentifiableSupplier<V> value);

    default <V> Supplier<V> useMemoSupplier(IdentifiableSupplier<V> value) {
        final V memoized = useMemo(value);
        return () -> memoized;
    }

    default <V> V useCallback(V fun) {
        return useCallback(fun, Collections.emptyList());
    }

    default <V> V useCallback(V fun, Object dependency) {
        return useCallback(fun, Collections.singletonList(dependency));
    }

    <V> V useCallback(V fun, List<Object> dependencies);

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
