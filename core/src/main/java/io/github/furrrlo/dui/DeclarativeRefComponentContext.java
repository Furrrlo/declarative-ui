package io.github.furrrlo.dui;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface DeclarativeRefComponentContext<T> extends DeclarativeComponentContext<T> {

    void ref(Ref<? super T> ref);

    void ref(Consumer<? super T> ref);

    <V> DeclarativeRefComponentContext<T> inner(Function<T, V> getter, DeclarativeComponent<V> component);

    default <V> DeclarativeRefComponentContext<T> attribute(String key, BiConsumer<T, V> setter, Supplier<V> value) {
        return attribute(key, setter, value, (c, oldV, newV) -> Objects.deepEquals(oldV, newV));
    }

    <V> DeclarativeRefComponentContext<T> attribute(String key,
                                                 BiConsumer<T, V> setter,
                                                 Supplier<V> value,
                                                 AttributeEqualityFn<T, V> equalityFn);

    <V, S extends DeclarativeComponentWithIdSupplier<? extends V>> DeclarativeRefComponentContext<T> listAttribute(
            String key,
            Class<V> type,
            ListReplacer<T, V, S> replacer,
            Supplier<List<V>> fn);

    <V, S extends DeclarativeComponentWithIdSupplier<? extends V>> DeclarativeRefComponentContext<T> listAttribute(
            String key,
            Class<V> type,
            ListRemover<T> remover,
            Supplier<List<V>> fn,
            ListAdder<T, V, S> adder);

    <C> DeclarativeRefComponentContext<T> fnAttribute(String key,
                                                   BiConsumer<T, C> setter,
                                                   DeclarativeComponentSupplier<C> fn);

    <C, S extends DeclarativeComponentWithIdSupplier<? extends C>> DeclarativeRefComponentContext<T> listFnAttribute(
            String key,
            ListSetter<T, C, S> setter,
            Supplier<List<S>> fn);

    <C, S extends DeclarativeComponentWithIdSupplier<? extends C>> DeclarativeRefComponentContext<T> listFnAttribute(
            String key,
            ListReplacer<T, C, S> replacer,
            Supplier<List<S>> fn);

    <C, S extends DeclarativeComponentWithIdSupplier<? extends C>> DeclarativeRefComponentContext<T> listFnAttribute(
            String key,
            ListAdder<T, C, S> adder,
            ListRemover<T> remover,
            Supplier<List<S>> fn);

    @FunctionalInterface
    interface AttributeEqualityFn<T, V> {

        boolean equals(T component, V prevV, V newV);

        static <T, V> AttributeEqualityFn<T, V> never() {
            return (c, oldV, newV) -> false;
        }
    }

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
