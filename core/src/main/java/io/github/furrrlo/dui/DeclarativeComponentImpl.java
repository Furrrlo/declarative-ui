package io.github.furrrlo.dui;

import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

class DeclarativeComponentImpl<T, O_CTX extends DeclarativeComponentContext<T>>
        extends StatefulDeclarativeComponent<T, T, O_CTX, DeclarativeComponentImpl.ContextImpl<T>> {

    private static final Logger LOGGER = Logger.getLogger(DeclarativeComponentImpl.class.getName());

    private final @Nullable DeclarativeComponentContextDecorator<T> decorator;
    private final Supplier<@Nullable T> componentFactory;
    private final @Nullable Class<T> componentType;
    private final BooleanSupplier canUpdateInCurrentThread;
    private final Consumer<Runnable> updateScheduler;
    private T component;

    public DeclarativeComponentImpl(Supplier<? extends DeclarativeComponentContextDecorator<T>> decoratorFactory,
                                    @Nullable Body<T, O_CTX> body) {
        this(decoratorFactory.get(), body);
    }

    private DeclarativeComponentImpl(DeclarativeComponentContextDecorator<T> decorator, @Nullable Body<T, O_CTX> body) {
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
                                    Consumer<Runnable> updateScheduler,
                                    @Nullable Body<T, DeclarativeComponentContext<T>> body) {
        super((Body<T, O_CTX>) body);
        this.decorator = null;
        this.componentType = componentType;
        this.componentFactory = componentFactory;
        this.updateScheduler = updateScheduler;
        this.canUpdateInCurrentThread = canUpdateInCurrentThread;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void copy(StatefulDeclarativeComponent<?, ?, ?, ?> other0) {
        super.copy(other0);

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
    public void triggerComponentUpdate() {
        updateScheduler.accept(this::updateComponent);
    }

    @Override
    public void runOrScheduleOnFrameworkThread(Runnable runnable) {
        if(canUpdateInCurrentThread.getAsBoolean())
            runnable.run();
        else
            updateScheduler.accept(runnable);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void invokeBody(Body<T, O_CTX> body, O_CTX newCtx) {
        if(decorator != null) {
            decorator.setToDecorate(newCtx);
            // This cast to C has to be guaranteed by the DeclarativeComponentFactory
            body.component((O_CTX) decorator);
            return;
        }

        super.invokeBody(body, newCtx);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void updateAttributes(ContextImpl<T> newCtx) {
        // TODO: what happens when an attribute was there, but is no longer present?
        newCtx.attributes.forEach((key, prop) -> {
            final Attr<T, ?> prevProp = context != null ?
                    context.attributes.get(key) :
                    null;
            final Object prevValue = prevProp != null ? prevProp.value() : null;
            ((Attr) prop).update(component, prevProp != null, prevProp, prevValue);
        });
    }

    @Override
    protected void disposeComponent() {
        LOGGER.log(Level.FINE, "Disposing component {0}", component);
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

        private final LinkedHashMap<String, Attr<T, ?>> attributes; // Important: this needs to maintain order

        public ContextImpl(DeclarativeComponentImpl<T, ?> outer) {
            super(outer);
            this.attributes = new LinkedHashMap<>();
        }

        public ContextImpl(DeclarativeComponentImpl<T, ?> outer, ContextImpl<T> other) {
            super(outer, other);
            this.attributes = other.attributes;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <V> DeclarativeComponentContext<T> inner(Function<T, V> getter, DeclarativeComponent<V> component) {
            ensureInsideBody();
            // I trust this cast
            StatefulDeclarativeComponent<V, V, DeclarativeComponentContext<V>, ?> internalComponent =
                    (StatefulDeclarativeComponent<V, V, DeclarativeComponentContext<V>, ?>) component.doApplyInternal();
            if (internalComponent.body != null) {
                internalComponent.isInvokingBody = true;
                internalComponent.invokeBody(internalComponent.body, new InnerComponentContextImpl<>(this, getter));
                internalComponent.isInvokingBody = false;
            }
            return this;
        }

        @Override
        public <V> DeclarativeComponentContext<T> attribute(String key, BiConsumer<T, V> setter, V value) {
            ensureInsideBody();
            attributes.put(key, new Attribute<>(key, setter, value));
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <V, S extends DeclarativeComponentWithIdSupplier<? extends V>> DeclarativeComponentContext<T> listAttribute(
                String key,
                Class<V> type,
                ListReplacer<T, V, S> replacer,
                List<V> fn
        ) {
            return listFnAttribute(
                    key,
                    (ListReplacer<T, V, SingleItem<V>>) replacer,
                    fn.stream().map(v -> new SingleItem<>(type, v)).collect(Collectors.collectingAndThen(
                            Collectors.toList(),
                            Collections::unmodifiableList)));
        }

        @Override
        @SuppressWarnings("unchecked")
        public <V, S extends DeclarativeComponentWithIdSupplier<? extends V>> DeclarativeComponentContext<T> listAttribute(
                String key,
                Class<V> type,
                ListRemover<T> remover,
                List<V> fn,
                ListAdder<T, V, S> adder
        ) {
            return listFnAttribute(
                    key,
                    (ListAdder<T, V, SingleItem<V>>) adder,
                    remover,
                    fn.stream().map(v -> new SingleItem<>(type, v)).collect(Collectors.collectingAndThen(
                            Collectors.toList(),
                            Collections::unmodifiableList)));
        }

        @Override
        public <C1> DeclarativeComponentContext<T> fnAttribute(String key, BiConsumer<T, C1> setter, DeclarativeComponentSupplier<C1> fn) {
            ensureInsideBody();
            attributes.put(key, new Attribute<>(key, setter, fn.doApplyInternal()));
            return this;
        }

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public <C, S extends DeclarativeComponentWithIdSupplier<? extends C>> DeclarativeComponentContext<T> listFnAttribute(
                String key,
                ListSetter<T, C, S> setter,
                List<S> fn
        ) {
            ensureInsideBody();
            attributes.put(key, new ListAttribute<>(
                    key,
                    setter,
                    fn,
                    (List) fn.stream()
                            .map(DeclarativeComponentSupplier::doApplyInternal)
                            .collect(Collectors.collectingAndThen(
                                    Collectors.toList(),
                                    Collections::unmodifiableList))));
            return this;
        }

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public <C, S extends DeclarativeComponentWithIdSupplier<? extends C>> DeclarativeComponentContext<T> listFnAttribute(
                String key,
                ListReplacer<T, C, S> replacer,
                List<S> fn
        ) {
            ensureInsideBody();
            attributes.put(key, new ReplacingListAttribute<>(
                    key,
                    replacer,
                    fn,
                    (List) fn.stream()
                            .map(DeclarativeComponentSupplier::doApplyInternal)
                            .collect(Collectors.collectingAndThen(
                                    Collectors.toList(),
                                    Collections::unmodifiableList))));
            return this;
        }

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public <C1, S extends DeclarativeComponentWithIdSupplier<? extends C1>> DeclarativeComponentContext<T> listFnAttribute(
                String key,
                ListAdder<T, C1, S> adder,
                ListRemover<T> remover,
                List<S> fn
        ) {
            ensureInsideBody();
            attributes.put(key, new DiffingListAttribute<T, C1, S>(
                    key,
                    adder, remover,
                    fn,
                    (List) fn.stream()
                            .map(DeclarativeComponentSupplier::doApplyInternal)
                            .collect(Collectors.collectingAndThen(
                                    Collectors.toList(),
                                    Collections::unmodifiableList))));
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

        void update(T obj, boolean wasSet, @Nullable SELF prev, @Nullable Object prevValue);

        Object value();
    }
}
