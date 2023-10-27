package io.github.furrrlo.dui;

import io.github.furrrlo.dui.DeclarativeComponentContextDecorator.ReservedMemoProxy;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

class DeclarativeComponentImpl<T, O_CTX extends DeclarativeComponentContext>
        extends StatefulDeclarativeComponent<T, O_CTX, DeclarativeComponentImpl.ContextImpl<T>> {

    private static final Logger LOGGER = Logger.getLogger(DeclarativeComponentImpl.class.getName());

    private final @Nullable DeclarativeComponentContextDecorator<T> decorator;
    private final Supplier<@Nullable T> componentFactory;
    private final @Nullable Class<T> componentType;
    private final BooleanSupplier canUpdateInCurrentThread;
    private final UpdateScheduler updateScheduler;
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
        this.updateScheduler = decorator.getUpdateScheduler();
        this.canUpdateInCurrentThread = decorator.getCanUpdateInCurrentThread();
    }

    @SuppressWarnings("unchecked")
    public DeclarativeComponentImpl(Class<T> componentType,
                                    Supplier<T> componentFactory,
                                    BooleanSupplier canUpdateInCurrentThread,
                                    UpdateScheduler updateScheduler,
                                    @Nullable IdentifiableConsumer<DeclarativeComponentContext> body) {
        super((IdentifiableConsumer<O_CTX>) body);
        this.decorator = null;
        this.componentType = componentType;
        this.componentFactory = componentFactory;
        this.updateScheduler = updateScheduler;
        this.canUpdateInCurrentThread = canUpdateInCurrentThread;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void substitute(StatefulDeclarativeComponent<?, ?, ?> other0) {
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
                body.getImplClass().getName() :
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
        scheduleOnFrameworkThread(COMPONENT_UPDATE_PRIORITY, () -> {
            StatefulDeclarativeComponent<?, ?, ?> sub = substituteComponentRef.get();
            if(sub != null)
                sub.updateComponent(UpdateFlags.FORCE);
        });
    }

    @Override
    public void scheduleOnFrameworkThread(int priority, Runnable runnable) {
        updateScheduler.schedule(priority, runnable);
    }

    @Override
    public void runOrScheduleOnFrameworkThread(Runnable runnable) {
        if(canUpdateInCurrentThread.getAsBoolean())
            runnable.run();
        else
            scheduleOnFrameworkThread(HIGHEST_PRIORITY, runnable);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void invokeBody(IdentifiableConsumer<O_CTX> body,
                              DeclarativeComponentContext newCtx,
                              Consumer<ReservedMemoProxy<?>> reserveMemo) {
        try {
            if(decorator != null) {
                if(!(newCtx instanceof DeclarativeRefComponentContext))
                    throw new UnsupportedOperationException("Trying to decorate a component which has no ref");

                final DeclarativeRefComponentContext<T> newRefCtx = (DeclarativeRefComponentContext<T>) newCtx;
                decorator.setToDecorate(newRefCtx, reserveMemo);
                try {
                    // This cast to C has to be guaranteed by the DeclarativeComponentFactory
                    body.accept((O_CTX) decorator);
                } finally {
                    decorator.endDecoration();
                }
            } else {
                super.invokeBody(body, newCtx, reserveMemo);
            }
        } finally {
            if(newCtx instanceof ContextImpl)
                ((ContextImpl<T>) newCtx).populateRefs(component);
        }
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
        this.<Void, A>buildOrChangeAttrWithStateDependency(attrKey, attr.updatePriority(), () -> {
            attr.update(this, obj, wasSet, prev, prevValue);
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

    private <RET, A extends Attr<T, A>> RET buildOrChangeAttrWithStateDependency(
            String attrKey, int updatePriority, Supplier<RET> factory) {

        // Notice how it's not capturing neither this nor attr, as both  might be replaced with
        // newer versions, and we do not want to update stale stuff
        IdentifiableRunnable stateDependency = this.<A>makeAttrStateDependency(
                attrKey,
                (c0, attr) -> c0.scheduleOnFrameworkThread(updatePriority, () -> {
                    // - If for any reason its parent component is scheduled before this, and its body is re-run
                    //   before we can get to the update below, the attribute would be substituted,
                    //   we have a stale attribute and the actual one would have been updated anyway,
                    //   so we have to avoid updating the stale one
                    // - If its parent component is scheduled before this and is substituted, but the body not
                    //   re-run as its deps have not changed, then this attribute is not stale, we still need
                    //   to update it
                    // - If the parent component was disposed, then c.substituteComponentRef.get() is null, we
                    //   don't need to update anything
                    final DeclarativeComponentImpl<T, O_CTX> c =
                            (DeclarativeComponentImpl<T, O_CTX>) c0.substituteComponentRef.get();
                    if(c == null || (c.context != null && c.context.attributes.get(attrKey) != attr))
                        return;

                    c.runAsComponentUpdate(() -> c.updateAttribute(attrKey, attr, c.component, true, attr, attr.value()));
                }),
                (c, attr) -> new Object[] { attr });
        return withStateDependency(stateDependency, factory);
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

    static class ContextImpl<T>
            extends StatefulDeclarativeComponent.StatefulContext
            implements DeclarativeRefComponentContext<T> {

        private final DeclarativeComponentImpl<T, ?> outer;
        private final LinkedHashMap<String, Attr<T, ?>> attributes; // Important: this needs to maintain order
        private final List<Consumer<? super T>> refsSetters;

        public ContextImpl(DeclarativeComponentImpl<T, ?> outer) {
            super(outer);
            this.outer = outer;
            this.attributes = new LinkedHashMap<>();
            this.refsSetters = new ArrayList<>();
        }

        public ContextImpl(DeclarativeComponentImpl<T, ?> outer, ContextImpl<T> other) {
            super(outer, other);
            this.outer = outer;
            this.attributes = other.attributes;
            this.refsSetters = other.refsSetters;
        }

        @Override
        public void ref(Ref<? super T> ref) {
            ref(Function.identity(), ref);
        }

        private <V> void ref(Function<T, V> getter, Ref<? super V> ref) {
            if(!(ref instanceof RefImpl))
                throw new UnsupportedOperationException("Ref was not created by the framework");
            final RefImpl<? super V> refImpl = (RefImpl<? super V>) ref;
            refsSetters.add(actualComponent -> refImpl.curr(getter.apply(actualComponent)));
        }

        @Override
        public void ref(Consumer<? super T> ref) {
            refsSetters.add(ref);
        }

        void populateRefs(T actualComponent) {
            this.refsSetters.forEach(ref -> ref.accept(actualComponent));
            this.refsSetters.clear();
        }

        @Override
        @SuppressWarnings("unchecked")
        public <V> DeclarativeRefComponentContext<T> inner(Function<T, V> getter, DeclarativeComponent<V> component) {
            ensureInsideBody();
            // I trust this cast
            StatefulDeclarativeComponent<V, DeclarativeComponentContext, ?> internalComponent =
                    (StatefulDeclarativeComponent<V, DeclarativeComponentContext, ?>) component.doApplyInternal();
            if (internalComponent.body != null) {
                internalComponent.isInvokingBody = true;
                internalComponent.invokeBody(
                        internalComponent.body,
                        new InnerComponentContextImpl<>(this, ref -> ref(getter, ref), getter),
                        this::reserveMemo);
                internalComponent.isInvokingBody = false;
            }
            return this;
        }

        @Override
        public <V> DeclarativeRefComponentContext<T> attribute(String key,
                                                               BiConsumer<T, V> setter,
                                                               Supplier<V> value,
                                                               AttributeEqualityFn<T, V> equalityFn) {
            ensureInsideBody();
            attributes.put(key, outer.buildOrChangeAttrWithStateDependency(
                    key, NORMAL_ATTRIBUTE_UPDATE_PRIORITY,
                    () -> new Attribute<>(key, NORMAL_ATTRIBUTE_UPDATE_PRIORITY, setter, value, equalityFn)));
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <V, S extends DeclarativeComponentWithIdSupplier<? extends V>> DeclarativeRefComponentContext<T> listAttribute(
                String key,
                Class<V> type,
                ListReplacer<T, V, S> replacer,
                Supplier<List<V>> fn
        ) {
            return doListFnAttribute(
                    key, NORMAL_ATTRIBUTE_UPDATE_PRIORITY,
                    (ListReplacer<T, V, SingleItem<V>>) replacer,
                    () -> fn.get().stream().map(v -> new SingleItem<>(type, v)).collect(Collectors.collectingAndThen(
                            Collectors.toList(),
                            Collections::unmodifiableList)));
        }

        @Override
        @SuppressWarnings("unchecked")
        public <V, S extends DeclarativeComponentWithIdSupplier<? extends V>> DeclarativeRefComponentContext<T> listAttribute(
                String key,
                Class<V> type,
                ListRemover<T> remover,
                Supplier<List<V>> fn,
                ListAdder<T, V, S> adder
        ) {
            return doListFnAttribute(
                    key, NORMAL_ATTRIBUTE_UPDATE_PRIORITY,
                    (ListAdder<T, V, SingleItem<V>>) adder,
                    remover,
                    () -> fn.get().stream().map(v -> new SingleItem<>(type, v)).collect(Collectors.collectingAndThen(
                            Collectors.toList(),
                            Collections::unmodifiableList)));
        }

        @Override
        public <C1> DeclarativeRefComponentContext<T> fnAttribute(String key, BiConsumer<T, C1> setter, DeclarativeComponentSupplier<C1> fn) {
            ensureInsideBody();
            attributes.put(key, new Attribute<>(
                    key, SUBCOMPONENT_ATTRIBUTE_UPDATE_PRIORITY,
                    setter, fn::doApplyInternal, AttributeEqualityFn.never()));
            return this;
        }

        @Override
        public <C, S extends DeclarativeComponentWithIdSupplier<? extends C>> DeclarativeRefComponentContext<T> listFnAttribute(
                String key,
                ListSetter<T, C, S> setter,
                Supplier<List<S>> fn
        ) {
            return doListFnAttribute(key, SUBCOMPONENT_ATTRIBUTE_UPDATE_PRIORITY, setter, fn);
        }

        @SuppressWarnings("unchecked")
        public <C, S extends DeclarativeComponentWithIdSupplier<? extends C>> DeclarativeRefComponentContext<T> doListFnAttribute(
                String key,
                int updatePriority,
                ListSetter<T, C, S> setter,
                Supplier<List<S>> fn
        ) {
            ensureInsideBody();
            attributes.put(key, outer.buildOrChangeAttrWithStateDependency(key, updatePriority, () -> new ListAttribute<>(
                    key,
                    updatePriority,
                    setter,
                    fn,
                    suppliers -> suppliers.stream()
                            .map(s -> (StatefulDeclarativeComponent<C, ?, ?>) s.doApplyInternal())
                            .collect(Collectors.collectingAndThen(
                                    Collectors.toList(),
                                    Collections::unmodifiableList)))));
            return this;
        }

        @Override
        public <C, S extends DeclarativeComponentWithIdSupplier<? extends C>> DeclarativeRefComponentContext<T> listFnAttribute(
                String key,
                ListReplacer<T, C, S> replacer,
                Supplier<List<S>> fn
        ) {
            return doListFnAttribute(key, SUBCOMPONENT_ATTRIBUTE_UPDATE_PRIORITY, replacer, fn);
        }

        @SuppressWarnings("unchecked")
        public <C, S extends DeclarativeComponentWithIdSupplier<? extends C>> DeclarativeRefComponentContext<T> doListFnAttribute(
                String key,
                int updatePriority,
                ListReplacer<T, C, S> replacer,
                Supplier<List<S>> fn
        ) {
            ensureInsideBody();
            attributes.put(key, outer.buildOrChangeAttrWithStateDependency(key, updatePriority, () -> new ReplacingListAttribute<>(
                    key,
                    updatePriority,
                    replacer,
                    fn,
                    suppliers -> suppliers.stream()
                            .map(s -> (StatefulDeclarativeComponent<C, ?, ?>) s.doApplyInternal())
                            .collect(Collectors.collectingAndThen(
                                    Collectors.toList(),
                                    Collections::unmodifiableList)))));
            return this;
        }

        @Override
        public <C, S extends DeclarativeComponentWithIdSupplier<? extends C>> DeclarativeRefComponentContext<T> listFnAttribute(
                String key,
                ListAdder<T, C, S> adder,
                ListRemover<T> remover,
                Supplier<List<S>> fn
        ) {
            return doListFnAttribute(key, SUBCOMPONENT_ATTRIBUTE_UPDATE_PRIORITY, adder, remover, fn);
        }

        @SuppressWarnings("unchecked")
        public <C, S extends DeclarativeComponentWithIdSupplier<? extends C>> DeclarativeRefComponentContext<T> doListFnAttribute(
                String key,
                int updatePriority,
                ListAdder<T, C, S> adder,
                ListRemover<T> remover,
                Supplier<List<S>> fn
        ) {
            ensureInsideBody();
            attributes.put(key, outer.buildOrChangeAttrWithStateDependency(key, updatePriority, () -> new DiffingListAttribute<>(
                    key,
                    updatePriority,
                    adder, remover,
                    fn,
                    suppliers -> suppliers.stream()
                            .map(s -> (StatefulDeclarativeComponent<C, ?, ?>) s.doApplyInternal())
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
                    new UpdateScheduler(Runnable::run),
                    null);
        }

        @Override
        public String getId() {
            return item.toString();
        }
    }

    interface Attr<T, SELF extends Attr<T, SELF>> {

        int updatePriority();

        Object value();

        void update(DeclarativeComponentImpl<T, ?> declarativeComponent,
                    T obj,
                    boolean wasSet,
                    @Nullable SELF prev,
                    @Nullable Object prevValue);

        void dispose();
    }
}
