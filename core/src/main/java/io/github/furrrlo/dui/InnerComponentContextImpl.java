package io.github.furrrlo.dui;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

class InnerComponentContextImpl<P, T> implements DeclarativeComponentContext<T> {

    private final DeclarativeComponentContext<P> parent;
    private final Function<P, T> childGetter;

    public InnerComponentContextImpl(DeclarativeComponentContext<P> parent, Function<P, T> childGetter) {
        this.parent = parent;
        this.childGetter = childGetter;
    }

    @Override
    public <V> State<V> useState(V value) {
        return parent.useState(value);
    }

    @Override
    public <V> State<V> useState(Supplier<V> value) {
        return parent.useState(value);
    }

    @Override
    public <V> V useMemo(Supplier<V> value) {
        return parent.useMemo(value);
    }

    @Override
    public <V> V useMemo(Supplier<V> value, Object dependency) {
        return parent.useMemo(value, dependency);
    }

    @Override
    public <V> V useMemo(Supplier<V> value, List<Object> dependencies) {
        return parent.useMemo(value, dependencies);
    }

    @Override
    public <V> V useCallback(V fun) {
        return parent.useCallback(fun);
    }

    @Override
    public <V> V useCallback(V fun, Object dependency) {
        return parent.useCallback(fun, dependency);
    }

    @Override
    public <V> V useCallback(V fun, List<Object> dependencies) {
        return parent.useCallback(fun, dependencies);
    }

    @Override
    public <V> DeclarativeComponentContext<T> inner(Function<T, V> getter, DeclarativeComponent<V> component) {
        parent.inner(parent -> getter.apply(this.childGetter.apply(parent)), component);
        return this;
    }

    @Override
    public <V> DeclarativeComponentContext<T> attribute(String key, BiConsumer<T, V> setter, Supplier<V> value) {
        parent.attribute(key, (parent, val) -> setter.accept(childGetter.apply(parent), val), value);
        return this;
    }

    @Override
    public <V, S extends DeclarativeComponentWithIdSupplier<? extends V>> DeclarativeComponentContext<T> listAttribute(
            String key,
            Class<V> type,
            ListReplacer<T, V, S> replacer,
            List<V> fn
    ) {
        parent.<V, S>listAttribute(key, type,
                (parent, idx, supplier, child) -> replacer.replace(childGetter.apply(parent), idx, supplier, child),
                fn);
        return this;
    }

    @Override
    public <V, S extends DeclarativeComponentWithIdSupplier<? extends V>> DeclarativeComponentContext<T> listAttribute(
            String key,
            Class<V> type,
            ListRemover<T> remover,
            List<V> fn,
            ListAdder<T, V, S> adder
    ) {
        parent.<V, S>listAttribute(key, type,
                (parent, idx) -> remover.remover(childGetter.apply(parent), idx),
                fn,
                (parent, idx, supplier, child) -> adder.add(childGetter.apply(parent), idx, supplier, child));
        return this;
    }

    @Override
    public <C> DeclarativeComponentContext<T> fnAttribute(String key,
                                                          BiConsumer<T, C> setter,
                                                          DeclarativeComponentSupplier<C> fn) {
        parent.fnAttribute(key, (parent, v) -> setter.accept(childGetter.apply(parent), v), fn);
        return this;
    }

    @Override
    public <C, S extends DeclarativeComponentWithIdSupplier<? extends C>> DeclarativeComponentContext<T> listFnAttribute(
            String key,
            ListSetter<T, C, S> setter,
            List<S> fn
    ) {
        parent.listFnAttribute(key, (parent, supplier, v) -> setter.set(childGetter.apply(parent), supplier, v), fn);
        return this;
    }

    @Override
    public <C, S extends DeclarativeComponentWithIdSupplier<? extends C>> DeclarativeComponentContext<T> listFnAttribute(
            String key,
            ListReplacer<T, C, S> replacer,
            List<S> fn
    ) {
        parent.listFnAttribute(key,
                (parent, idx, supplier, child) -> replacer.replace(childGetter.apply(parent), idx, supplier, child),
                fn);
        return this;
    }

    @Override
    public <C, S extends DeclarativeComponentWithIdSupplier<? extends C>> DeclarativeComponentContext<T> listFnAttribute(
            String key,
            ListAdder<T, C, S> adder,
            ListRemover<T> remover,
            List<S> fn
    ) {
        parent.listFnAttribute(key,
                (parent, idx, supplier, child) -> adder.add(childGetter.apply(parent), idx, supplier, child),
                (parent, idx) -> remover.remover(childGetter.apply(parent), idx),
                fn);
        return this;
    }
}
