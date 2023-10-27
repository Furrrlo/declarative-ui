package io.github.furrrlo.dui;

import java.io.Serializable;
import java.util.List;
import java.util.function.*;

class InnerComponentContextImpl<P, T> implements DeclarativeRefComponentContext<T> {

    private final DeclarativeRefComponentContext<P> parent;
    private final Function<P, T> childGetter;
    private final Consumer<Ref<? super T>> registerRefs;

    public InnerComponentContextImpl(DeclarativeRefComponentContext<P> parent,
                                     Consumer<Ref<? super T>> registerRefs,
                                     Function<P, T> childGetter) {
        this.parent = parent;
        this.childGetter = childGetter;
        this.registerRefs = registerRefs;
    }

    @Override
    public void ref(Ref<? super T> ref) {
        registerRefs.accept(ref);
    }

    @Override
    public void ref(Consumer<? super T> ref) {
        parent.ref(p -> ref.accept(childGetter.apply(p)));
    }

    @Override
    public <V> State<V> useState(V value) {
        return parent.useState(value);
    }

    @Override
    public <V> State<V> useState(V value, BiPredicate<V, V> equalityFn) {
        return parent.useState(value, equalityFn);
    }

    @Override
    public <V> State<V> useState(Supplier<V> value) {
        return parent.useState(value);
    }

    @Override
    public <V> State<V> useState(Supplier<V> value, BiPredicate<V, V> equalityFn) {
        return parent.useState(value, equalityFn);
    }

    @Override
    public <V> Memo<V> useMemo(IdentifiableSupplier<V> value) {
        return parent.useMemo(value);
    }

    @Override
    public <V> Memo<V> useMemo(IdentifiableSupplier<V> value, BiPredicate<V, V> equalityFn) {
        return parent.useMemo(value, equalityFn);
    }

    @Override
    public <V extends Serializable> V useCallback(V fun) {
        return parent.useCallback(fun);
    }

    @Override
    public <V> V useCallbackExplicit(V fun, Object dependency) {
        return parent.useCallbackExplicit(fun, dependency);
    }

    @Override
    public <V> V useCallbackExplicit(V fun, List<Object> dependencies) {
        return parent.useCallbackExplicit(fun, dependencies);
    }

    @Override
    public <V> Ref<V> useRef(Supplier<V> fallbackValue) {
        return parent.useRef(fallbackValue);
    }

    @Override
    public void useLaunchedEffect(IdentifiableThrowingRunnable effect) {
        parent.useLaunchedEffect(effect);
    }

    @Override
    public void useDisposableEffect(IdentifiableConsumer<SetOnDisposeFn> effect) {
        parent.useDisposableEffect(effect);
    }

    @Override
    public void useSideEffect(Runnable effect) {
        parent.useSideEffect(effect);
    }

    @Override
    public <V> DeclarativeRefComponentContext<T> inner(Function<T, V> getter, DeclarativeComponent<V> component) {
        parent.inner(parent -> getter.apply(this.childGetter.apply(parent)), component);
        return this;
    }

    @Override
    public <V> DeclarativeRefComponentContext<T> attribute(String key, BiConsumer<T, V> setter, Supplier<V> value) {
        parent.attribute(key, (parent, val) -> setter.accept(childGetter.apply(parent), val), value);
        return this;
    }

    @Override
    public <V> DeclarativeRefComponentContext<T> attribute(String key,
                                                           BiConsumer<T, V> setter,
                                                           Supplier<V> value,
                                                           AttributeEqualityFn<T, V> equalityFn) {
        parent.attribute(
                key,
                (parent, val) -> setter.accept(childGetter.apply(parent), val),
                value,
                (parent, prevV, currV) -> equalityFn.equals(childGetter.apply(parent), prevV, currV));
        return this;
    }

    @Override
    public <V, S extends DeclarativeComponentWithIdSupplier<? extends V>> DeclarativeRefComponentContext<T> listAttribute(
            String key,
            Class<V> type,
            ListReplacer<T, V, S> replacer,
            Supplier<List<V>> fn
    ) {
        parent.<V, S>listAttribute(key, type,
                (parent, idx, supplier, child) -> replacer.replace(childGetter.apply(parent), idx, supplier, child),
                fn);
        return this;
    }

    @Override
    public <V, S extends DeclarativeComponentWithIdSupplier<? extends V>> DeclarativeRefComponentContext<T> listAttribute(
            String key,
            Class<V> type,
            ListRemover<T> remover,
            Supplier<List<V>> fn,
            ListAdder<T, V, S> adder
    ) {
        parent.<V, S>listAttribute(key, type,
                (parent, idx) -> remover.remover(childGetter.apply(parent), idx),
                fn,
                (parent, idx, supplier, child) -> adder.add(childGetter.apply(parent), idx, supplier, child));
        return this;
    }

    @Override
    public <C> DeclarativeRefComponentContext<T> fnAttribute(String key,
                                                             BiConsumer<T, C> setter,
                                                             DeclarativeComponentSupplier<C> fn) {
        parent.fnAttribute(key, (parent, v) -> setter.accept(childGetter.apply(parent), v), fn);
        return this;
    }

    @Override
    public <C, S extends DeclarativeComponentWithIdSupplier<? extends C>> DeclarativeRefComponentContext<T> listFnAttribute(
            String key,
            ListSetter<T, C, S> setter,
            Supplier<List<S>> fn
    ) {
        parent.listFnAttribute(key, (parent, supplier, v) -> setter.set(childGetter.apply(parent), supplier, v), fn);
        return this;
    }

    @Override
    public <C, S extends DeclarativeComponentWithIdSupplier<? extends C>> DeclarativeRefComponentContext<T> listFnAttribute(
            String key,
            ListReplacer<T, C, S> replacer,
            Supplier<List<S>> fn
    ) {
        parent.listFnAttribute(key,
                (parent, idx, supplier, child) -> replacer.replace(childGetter.apply(parent), idx, supplier, child),
                fn);
        return this;
    }

    @Override
    public <C, S extends DeclarativeComponentWithIdSupplier<? extends C>> DeclarativeRefComponentContext<T> listFnAttribute(
            String key,
            ListAdder<T, C, S> adder,
            ListRemover<T> remover,
            Supplier<List<S>> fn
    ) {
        parent.listFnAttribute(key,
                (parent, idx, supplier, child) -> adder.add(childGetter.apply(parent), idx, supplier, child),
                (parent, idx) -> remover.remover(childGetter.apply(parent), idx),
                fn);
        return this;
    }
}
