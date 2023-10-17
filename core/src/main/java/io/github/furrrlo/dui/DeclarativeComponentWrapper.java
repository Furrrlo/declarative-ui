package io.github.furrrlo.dui;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

class DeclarativeComponentWrapper<R> extends StatefulDeclarativeComponent<
        Object, R, DeclarativeComponentContext<Object>, StatefulDeclarativeComponent.StatefulContext<Object>> {


    private final Function<DeclarativeComponentContext<?>, DeclarativeComponentSupplier<R>> wrapperBody;

    private boolean wasDeepUpdated;
    private @Nullable StatefulDeclarativeComponent<?, R, ?, ?> wrapped;
    private @Nullable StatefulDeclarativeComponent<?, R, ?, ?> prevWrapped;

    public DeclarativeComponentWrapper(IdentifiableFunction<DeclarativeComponentContext<?>, DeclarativeComponentSupplier<R>> body) {
        this(body, new AtomicReference<>());
    }

    private DeclarativeComponentWrapper(IdentifiableFunction<DeclarativeComponentContext<?>, DeclarativeComponentSupplier<R>> body,
                                        AtomicReference<DeclarativeComponentWrapper<R>> selfRef) {
        super(IdentifiableConsumer.explicit((DeclarativeComponentContext<Object> ctx) -> {
            final DeclarativeComponentWrapper<R> self = Objects.requireNonNull(
                    selfRef.get(),
                    "Body invoked before wrapper could set a reference to itself");
            self.invokeWrappedBody(ctx);
        }, body.deps()));
        this.wrapperBody = body;
        selfRef.set(this);
    }

    private void invokeWrappedBody(DeclarativeComponentContext<Object> ctx) {
        prevWrapped = wrapped;
        wrapped = wrapperBody.apply(ctx).doApplyInternal();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void substitute(StatefulDeclarativeComponent<?, ?, ?, ?> other0) {
        super.substitute(other0);

        final DeclarativeComponentWrapper<R> other = (DeclarativeComponentWrapper<R>) other0;
        wasDeepUpdated = other.wasDeepUpdated;
        wrapped = other.wrapped;
        prevWrapped = other.prevWrapped;
    }

    @Override
    protected StatefulContext<Object> newContext() {
        return new StatefulContext<>(this);
    }

    @Override
    protected void copyContext(@Nullable StatefulContext<Object> toCopy) {
        context = toCopy == null ? null : new StatefulContext<>(this, toCopy);
    }

    public String getDeclarativeWrapperType() {
        return wrapperBody.getClass().getName();
    }

    @Override
    public String getDeclarativeType() {
        final StatefulDeclarativeComponent<?, R, ?, ?> wrapped = Objects.requireNonNull(
                this.wrapped,
                "getDeclarativeType() attribute called without having invoked the wrapper body");
        return getDeclarativeWrapperType() + "[" + wrapped.getDeclarativeType() + "]";
    }

    @Override
    protected void updateComponent(int flags) {
        super.updateComponent(flags);

        // Wrappers need to invoke their body before they can say declarativeType
        // so if this was soft-updated, propagate the update to the child wrapper
        final boolean deepUpdate = (flags & UpdateFlags.SOFT) == 0;
        if(!deepUpdate && wrapped instanceof DeclarativeComponentWrapper) {
            if (prevWrapped instanceof DeclarativeComponentWrapper && Objects.equals(
                    ((DeclarativeComponentWrapper<?>) wrapped).getDeclarativeWrapperType(),
                    Objects.requireNonNull((DeclarativeComponentWrapper<?>) prevWrapped).getDeclarativeWrapperType()))
                wrapped.substitute(prevWrapped);
            wrapped.updateComponent(UpdateFlags.SOFT);
        }
    }

    @Override
    public R getComponent() {
        final StatefulDeclarativeComponent<?, R, ?, ?> wrapped = Objects.requireNonNull(
                this.wrapped,
                "getComponent(...) attribute called without having invoked the wrapper body");
        return wrapped.getComponent();
    }

    @Override
    public void triggerStateUpdate() {
        // We do not want to eagerly execute the component update as possibly there are other updates
        // already scheduled in the framework specific scheduler, and we should respect that order,
        // so try to schedule the update to be executed on said framework scheduler

        StatefulDeclarativeComponent<?, ?, ?, ?> comp = wrapped;
        // Special case for nested wrappers, go on until we get to an actual component
        while (comp instanceof DeclarativeComponentWrapper)
            comp = ((DeclarativeComponentWrapper<?>) comp).wrapped;

        if(comp != null) {
            comp.scheduleOnFrameworkThread(() -> {
                StatefulDeclarativeComponent<?, ?, ?, ?> sub = substituteComponentRef.get();
                if(sub != null)
                    sub.updateComponent(UpdateFlags.FORCE);
            });
        } else {
            updateComponent(UpdateFlags.FORCE);
        }
    }

    @Override
    public void scheduleOnFrameworkThread(Runnable runnable) {
        runnable.run();
    }

    @Override
    public void runOrScheduleOnFrameworkThread(Runnable runnable) {
        runnable.run();
    }

    @Override
    protected void updateAttributes(StatefulContext<Object> newCtx) {
        final StatefulDeclarativeComponent<?, R, ?, ?> curr = Objects.requireNonNull(
                wrapped,
                "updateAttributes(...) called without having invoked the wrapper body");

        final boolean wasDeepUpdated = this.wasDeepUpdated;
        this.wasDeepUpdated = true;
        Attribute.updateDeclarativeComponent(
                wasDeepUpdated,
                curr,
                prevWrapped,
                null,
                null);
    }

    @Override
    protected void disposeComponent() {
        super.disposeComponent();
        if(wrapped != null)
            wrapped.runOrScheduleOnFrameworkThread(wrapped::disposeComponent);
    }
}
