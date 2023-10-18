package io.github.furrrlo.dui;

import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

class DeclarativeComponentImpl<T, O_CTX extends DeclarativeComponentContext<T>>
        extends StatefulDeclarativeComponent<T, T, O_CTX, DeclarativeComponentImpl.ContextImpl<T>> {

    private static final Logger LOGGER = Logger.getLogger(DeclarativeComponentImpl.class.getName());
    private static final boolean TRACE_UPDATE_SCHEDULES = true;

    private final @Nullable DeclarativeComponentContextDecorator<T> decorator;
    private final Supplier<@Nullable T> componentFactory;
    private final @Nullable Class<T> componentType;
    private final BooleanSupplier canUpdateInCurrentThread;
    private final Consumer<Runnable> updateScheduler;
    private T component;

    public DeclarativeComponentImpl(Supplier<? extends DeclarativeComponentContextDecorator<T>> decoratorFactory,
                                    @Nullable IdentifiableConsumer<O_CTX> body) {
        this(decoratorFactory.get(), body);
    }

    private DeclarativeComponentImpl(DeclarativeComponentContextDecorator<T> decorator, @Nullable IdentifiableConsumer<O_CTX> body) {
        super(body);
        this.decorator = decorator;
        this.componentType = decorator.getType();
        this.componentFactory = decorator.getFactory();
        this.updateScheduler = traceUpdateSchedules(decorator.getUpdateScheduler());
        this.canUpdateInCurrentThread = decorator.getCanUpdateInCurrentThread();
    }

    @SuppressWarnings("unchecked")
    public DeclarativeComponentImpl(Class<T> componentType,
                                    Supplier<T> componentFactory,
                                    BooleanSupplier canUpdateInCurrentThread,
                                    Consumer<Runnable> updateScheduler,
                                    @Nullable IdentifiableConsumer<DeclarativeComponentContext<T>> body) {
        super((IdentifiableConsumer<O_CTX>) body);
        this.decorator = null;
        this.componentType = componentType;
        this.componentFactory = componentFactory;
        this.updateScheduler = traceUpdateSchedules(updateScheduler);
        this.canUpdateInCurrentThread = canUpdateInCurrentThread;
    }

    private static Consumer<Runnable> traceUpdateSchedules(Consumer<Runnable> updateScheduler) {
        return !TRACE_UPDATE_SCHEDULES ? updateScheduler : (update) -> {
            final Throwable trace = new Exception("Called from here");
            updateScheduler.accept(() -> {
                try {
                    update.run();
                } catch (Throwable ex) {
                    ex.addSuppressed(trace);
                    throw ex;
                }
            });
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void substitute(StatefulDeclarativeComponent<?, ?, ?, ?> other0) {
        super.substitute(other0);

        final DeclarativeComponentImpl<T, ?> other = (DeclarativeComponentImpl<T, ?>) other0;
        ensureSame("component type", other, fn -> fn.componentType);
        ensureSame("component factory", other, fn -> fn.componentFactory.getClass());
        ensureSame("decorator", other, fn -> fn.decorator != null ? fn.decorator.getClass() : null);

        component = other.component;
    }

    @Override
    protected ContextImpl<T> newContext() {
        return new ContextImpl<>(this);
    }

    @Override
    protected void copyContext(@Nullable ContextImpl<T> toCopy) {
        context = toCopy == null ? null : new ContextImpl<>(this, toCopy);
    }

    @Override
    public @Nullable String getDeclarativeType() {
        return body != null ?
                body.getClass().getName() :
                componentType != null ? componentType.getName() : null;
    }

    @Override
    public T getComponent() {
        return component;
    }

    @Override
    public T updateOrCreateComponent() {
        if(component == null) {
            component = componentFactory.get();
            LOGGER.log(Level.FINE, "Created component {0}", component);
        }

        return super.updateOrCreateComponent();
    }

    @Override
    public void triggerStateUpdate() {
        updateScheduler.accept(() -> {
            StatefulDeclarativeComponent<?, ?, ?, ?> sub = substituteComponentRef.get();
            if(sub != null)
                sub.updateComponent(UpdateFlags.FORCE);
        });
    }

    @Override
    public void scheduleOnFrameworkThread(Runnable runnable) {
        updateScheduler.accept(runnable);
    }

    @Override
    public void runOrScheduleOnFrameworkThread(Runnable runnable) {
        if(canUpdateInCurrentThread.getAsBoolean())
            runnable.run();
        else
            scheduleOnFrameworkThread(runnable);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void invokeBody(IdentifiableConsumer<O_CTX> body,
                              DeclarativeComponentImpl.ContextImpl<T> underlyingNewCtx,
                              O_CTX newCtx) {
        if(decorator != null) {
            decorator.setToDecorate(newCtx, underlyingNewCtx::reserveMemo);
            try {
                // This cast to C has to be guaranteed by the DeclarativeComponentFactory
                body.accept((O_CTX) decorator);
            } finally {
                decorator.endDecoration();
            }
            return;
        }

        super.invokeBody(body, underlyingNewCtx, newCtx);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void updateAttributes(ContextImpl<T> newCtx) {
        newCtx.attributes.forEach((key, prop) -> {
            final Attr<T, ?> prevProp = context != null ?
                    context.attributes.get(key) :
                    null;
            final Object prevValue = prevProp != null ? prevProp.value() : null;
            this.<Attr>updateAttribute(key, prop, component, prevProp != null, prevProp, prevValue);
        });
        // TODO: what happens when an attribute was there, but is no longer present?
        // for now copy over old attributes (might be not ideal, but at least we don't lose stuff around)
        if(context != null)
            context.attributes.forEach(newCtx.attributes::putIfAbsent);
    }

    private <A extends Attr<T, A>>  void updateAttribute(String attrKey,
                                                         A attr,
                                                         T obj,
                                                         boolean wasSet,
                                                         @Nullable A prev,
                                                         @Nullable Object prevValue) {
        this.<Void, A>buildOrChangeAttrWithStateDependency(attrKey, () -> {
            attr.update(obj, wasSet, prev, prevValue);
            return null;
        });
    }

    @SuppressWarnings("unchecked")
    public <A extends Attr<?, A>> IdentifiableRunnable makeAttrStateDependency(
            String attrKey,
            BiConsumer<DeclarativeComponentImpl<T, O_CTX>, A> runnable,
            BiFunction<DeclarativeComponentImpl<T, O_CTX>, A, Object[]> deps) {
        return this.<DeclarativeComponentImpl<T, O_CTX>>makeStateDependency(
                c -> {
                    final Attr<?, ?> attr;
                    if(c.context != null && (attr = c.context.attributes.get(attrKey)) != null)
                        runnable.accept(c, (A) attr);
                },
                c -> {
                    final Attr<?, ?> attr;
                    if(c.context != null && (attr = c.context.attributes.get(attrKey)) != null)
                        return deps.apply(c, (A) attr);
                    return new Object[] { c, attrKey };
                });
    }

    private <RET, A extends Attr<T, A>> RET buildOrChangeAttrWithStateDependency(String attrKey, Supplier<RET> factory) {
        IdentifiableRunnable prevStateDependency = currentStateDependency;
        // Notice how it's not capturing neither this nor attr, as both  might be replaced with
        // newer versions, and we do not want to update stale stuff
        this.currentStateDependency = this.<A>makeAttrStateDependency(
                attrKey,
                (c, attr) -> c.updateScheduler.accept(() -> c.runAsComponentUpdate(() ->
                        c.updateAttribute(attrKey, attr, c.component, true, attr, attr.value()))),
                (c, attr) -> new Object[] { attr });
        try {
            return factory.get();
        } finally {
            this.currentStateDependency = prevStateDependency;
        }
    }

    @Override
    protected void disposeComponent() {
        super.disposeComponent();

        LOGGER.log(Level.FINE, "Disposing component {0}", component);

        if(context != null)
            context.attributes.values().forEach(Attr::dispose);

        // TODO: how to make components disposable?
        if(component instanceof Window) {
            final Window window = (Window) component;
            window.setVisible(false);
            window.dispose();
        }

        component = null;
    }

    @Override
    public String toString() {
        return "DeclarativeComponentImpl{" +
                "decorator=" + decorator +
                ", componentType=" + componentType +
                ", componentFactory=" + componentFactory +
                ", component=" + component +
                "} " + super.toString();
    }

    static class ContextImpl<T> extends StatefulDeclarativeComponent.StatefulContext<T> {

        private final DeclarativeComponentImpl<T, ?> outer;
        private final LinkedHashMap<String, Attr<T, ?>> attributes; // Important: this needs to maintain order

        public ContextImpl(DeclarativeComponentImpl<T, ?> outer) {
            super(outer);
            this.outer = outer;
            this.attributes = new LinkedHashMap<>();
        }

        public ContextImpl(DeclarativeComponentImpl<T, ?> outer, ContextImpl<T> other) {
            super(outer, other);
            this.outer = outer;
            this.attributes = other.attributes;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <V> DeclarativeComponentContext<T> inner(Function<T, V> getter, DeclarativeComponent<V> component) {
            ensureInsideBody();
            // I trust this cast
            StatefulDeclarativeComponent<V, V, DeclarativeComponentContext<V>, ContextImpl<V>> internalComponent =
                    (StatefulDeclarativeComponent<V, V, DeclarativeComponentContext<V>, ContextImpl<V>>) component.doApplyInternal();
            if (internalComponent.body != null) {
                internalComponent.isInvokingBody = true;
                internalComponent.invokeBody(
                        internalComponent.body,
                        (ContextImpl<V>) this, // This cast is also incredibly unsafe
                        new InnerComponentContextImpl<>(this, getter));
                internalComponent.isInvokingBody = false;
            }
            return this;
        }

        @Override
        public <V> DeclarativeComponentContext<T> attribute(String key,
                                                            BiConsumer<T, V> setter,
                                                            Supplier<V> value,
                                                            AttributeEqualityFn<T, V> equalityFn) {
            ensureInsideBody();
            attributes.put(key, outer.buildOrChangeAttrWithStateDependency(
                    key,
                    () -> new Attribute<>(key, setter, value, equalityFn)));
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <V, S extends DeclarativeComponentWithIdSupplier<? extends V>> DeclarativeComponentContext<T> listAttribute(
                String key,
                Class<V> type,
                ListReplacer<T, V, S> replacer,
                Supplier<List<V>> fn
        ) {
            return listFnAttribute(
                    key,
                    (ListReplacer<T, V, SingleItem<V>>) replacer,
                    () -> fn.get().stream().map(v -> new SingleItem<>(type, v)).collect(Collectors.collectingAndThen(
                            Collectors.toList(),
                            Collections::unmodifiableList)));
        }

        @Override
        @SuppressWarnings("unchecked")
        public <V, S extends DeclarativeComponentWithIdSupplier<? extends V>> DeclarativeComponentContext<T> listAttribute(
                String key,
                Class<V> type,
                ListRemover<T> remover,
                Supplier<List<V>> fn,
                ListAdder<T, V, S> adder
        ) {
            return listFnAttribute(
                    key,
                    (ListAdder<T, V, SingleItem<V>>) adder,
                    remover,
                    () -> fn.get().stream().map(v -> new SingleItem<>(type, v)).collect(Collectors.collectingAndThen(
                            Collectors.toList(),
                            Collections::unmodifiableList)));
        }

        @Override
        public <C1> DeclarativeComponentContext<T> fnAttribute(String key, BiConsumer<T, C1> setter, DeclarativeComponentSupplier<C1> fn) {
            ensureInsideBody();
            attributes.put(key, new Attribute<>(key, setter, fn::doApplyInternal, AttributeEqualityFn.never()));
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <C, S extends DeclarativeComponentWithIdSupplier<? extends C>> DeclarativeComponentContext<T> listFnAttribute(
                String key,
                ListSetter<T, C, S> setter,
                Supplier<List<S>> fn
        ) {
            ensureInsideBody();
            attributes.put(key, outer.buildOrChangeAttrWithStateDependency(key, () -> new ListAttribute<>(
                    key,
                    setter,
                    fn,
                    suppliers -> suppliers.stream()
                            .map(s -> (StatefulDeclarativeComponent<?, C, ?, ?>) s.doApplyInternal())
                            .collect(Collectors.collectingAndThen(
                                    Collectors.toList(),
                                    Collections::unmodifiableList)))));
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <C, S extends DeclarativeComponentWithIdSupplier<? extends C>> DeclarativeComponentContext<T> listFnAttribute(
                String key,
                ListReplacer<T, C, S> replacer,
                Supplier<List<S>> fn
        ) {
            ensureInsideBody();
            attributes.put(key, outer.buildOrChangeAttrWithStateDependency(key, () -> new ReplacingListAttribute<>(
                    key,
                    replacer,
                    fn,
                    suppliers -> suppliers.stream()
                            .map(s -> (StatefulDeclarativeComponent<?, C, ?, ?>) s.doApplyInternal())
                            .collect(Collectors.collectingAndThen(
                                    Collectors.toList(),
                                    Collections::unmodifiableList)))));
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <C, S extends DeclarativeComponentWithIdSupplier<? extends C>> DeclarativeComponentContext<T> listFnAttribute(
                String key,
                ListAdder<T, C, S> adder,
                ListRemover<T> remover,
                Supplier<List<S>> fn
        ) {
            ensureInsideBody();
            attributes.put(key, outer.buildOrChangeAttrWithStateDependency(key, () -> new DiffingListAttribute<>(
                    key,
                    adder, remover,
                    fn,
                    suppliers -> suppliers.stream()
                            .map(s -> (StatefulDeclarativeComponent<?, C, ?, ?>) s.doApplyInternal())
                            .collect(Collectors.collectingAndThen(
                                    Collectors.toList(),
                                    Collections::unmodifiableList)))));
            return this;
        }
    }

    private static class SingleItem<T> implements DeclarativeComponentWithIdSupplier<T> {

        private final Class<T> type;
        private final T item;

        public SingleItem(Class<T> type, T item) {
            this.type = type;
            this.item = item;
        }

        @Override
        public DeclarativeComponent<T> apply(DeclarativeComponentFactory factory) {
            return new DeclarativeComponentImpl<>(
                    type,
                    () -> item,
                    // This in theory should never need to update any props anyway
                    () -> true,
                    Runnable::run,
                    null);
        }

        @Override
        public String getId() {
            return item.toString();
        }
    }

    interface Attr<T, SELF extends Attr<T, SELF>> {

        Object value();

        void update(T obj, boolean wasSet, @Nullable SELF prev, @Nullable Object prevValue);

        void dispose();
    }
}
