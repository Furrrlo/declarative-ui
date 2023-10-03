package io.github.furrrlo.dui;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.*;

public abstract class DeclarativeComponentContextDecorator<T> implements DeclarativeComponentContext<T> {

    private final @Nullable Class<T> type;
    private final Supplier<@Nullable T> factory;
    private final BooleanSupplier canUpdateInCurrentThread;
    private final Consumer<Runnable> updateScheduler;

    private @Nullable DeclarativeComponentContext<T> toDecorate;

    protected DeclarativeComponentContextDecorator(@Nullable Class<T> type,
                                                   Supplier<@Nullable T> factory,
                                                   BooleanSupplier canUpdateInCurrentThread,
                                                   Consumer<Runnable> updateScheduler) {
        this.type = type;
        this.factory = factory;
        this.canUpdateInCurrentThread = canUpdateInCurrentThread;
        this.updateScheduler = updateScheduler;
    }

    void setToDecorate(@Nullable DeclarativeComponentContext<T> toDecorate) {
        this.toDecorate = toDecorate;
    }

    private DeclarativeComponentContext<T> toDecorate() {
        return Objects.requireNonNull(
                toDecorate,
                "Missing object to decorate");
    }

    public @Nullable Class<T> getType() {
        return type;
    }

    public Supplier<@Nullable T> getFactory() {
        return factory;
    }

    public Consumer<Runnable> getUpdateScheduler() {
        return updateScheduler;
    }

    public BooleanSupplier getCanUpdateInCurrentThread() {
        return canUpdateInCurrentThread;
    }

    public DeclarativeComponentFactory fn() {
        return DeclarativeComponentFactory.INSTANCE;
    }

    @Override
    public <V> State<V> useState(V value) {
        return toDecorate().useState(value);
    }

    @Override
    public <V> State<V> useState(Supplier<V> value) {
        return toDecorate().useState(value);
    }

    @Override
    public <V> V useMemo(IdentifiableSupplier<V> value) {
        return toDecorate().useMemo(value);
    }

    @Override
    public <V> V useCallback(V fun) {
        return toDecorate().useCallback(fun);
    }

    @Override
    public <V> V useCallback(V fun, Object dependency) {
        return toDecorate().useCallback(fun, dependency);
    }

    @Override
    public <V> V useCallback(V fun, List<Object> dependencies) {
        return toDecorate().useCallback(fun, dependencies);
    }

    @Override
    public <V> DeclarativeComponentContext<T> inner(Function<T, V> getter, DeclarativeComponent<V> component) {
        return toDecorate().inner(getter, component);
    }

    @Override
    public <V> DeclarativeComponentContext<T> attribute(String key, BiConsumer<T, V> setter, Supplier<V> value) {
        return toDecorate().attribute(key, setter, value);
    }

    @Override
    public <V, S extends DeclarativeComponentWithIdSupplier<? extends V>> DeclarativeComponentContext<T> listAttribute(
            String key,
            Class<V> type,
            ListReplacer<T, V, S> replacer,
            List<V> fn
    ) {
        return toDecorate().listAttribute(key, type, replacer, fn);
    }

    @Override
    public <V, S extends DeclarativeComponentWithIdSupplier<? extends V>> DeclarativeComponentContext<T> listAttribute(
            String key,
            Class<V> type,
            ListRemover<T> remover,
            List<V> fn,
            ListAdder<T, V, S> adder
    ) {
        return toDecorate().listAttribute(key, type, remover, fn, adder);
    }

    @Override
    public <C> DeclarativeComponentContext<T> fnAttribute(String key, BiConsumer<T, C> setter, DeclarativeComponentSupplier<C> fn) {
        return toDecorate().fnAttribute(key, setter, fn);
    }

    @Override
    public <C, S extends DeclarativeComponentWithIdSupplier<? extends C>> DeclarativeComponentContext<T> listFnAttribute(
            String key,
            ListSetter<T, C, S> setter,
            List<S> fn
    ) {
        return toDecorate().listFnAttribute(key, setter, fn);
    }

    @Override
    public <C, S extends DeclarativeComponentWithIdSupplier<? extends C>> DeclarativeComponentContext<T> listFnAttribute(
            String key,
            ListReplacer<T, C, S> replacer,
            List<S> fn
    ) {
        return toDecorate().listFnAttribute(key, replacer, fn);
    }

    @Override
    public <C, S extends DeclarativeComponentWithIdSupplier<? extends C>> DeclarativeComponentContext<T> listFnAttribute(
            String key,
            ListAdder<T, C, S> adder,
            ListRemover<T> remover,
            List<S> fn
    ) {
        return toDecorate().listFnAttribute(key, adder, remover, fn);
    }
}
