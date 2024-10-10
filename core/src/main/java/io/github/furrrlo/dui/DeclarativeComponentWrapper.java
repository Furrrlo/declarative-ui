package io.github.furrrlo.dui;

import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

class DeclarativeComponentWrapper<R> extends StatefulDeclarativeComponent<
        R, DeclarativeComponentContext, StatefulDeclarativeComponent.StatefulContext> {


    private final IdentityFreeFunction<DeclarativeComponentContext, DeclarativeComponentSupplier<? extends R>> wrapperBody;

    private boolean wasDeepUpdated;
    private boolean isDeepUpdated;
    private @Nullable StatefulDeclarativeComponent<? extends R, ?, ?> wrapped;
    private @Nullable StatefulDeclarativeComponent<? extends R, ?, ?> prevWrapped;

    public DeclarativeComponentWrapper(IdentityFreeFunction<DeclarativeComponentContext, DeclarativeComponentSupplier<? extends R>> body) {
        this(IdentityFreeFunction.explicit(currentLookups(), body), new AtomicReference<>());
    }

    private DeclarativeComponentWrapper(IdentityFreeFunction<DeclarativeComponentContext, DeclarativeComponentSupplier<? extends R>> body,
                                        AtomicReference<DeclarativeComponentWrapper<R>> selfRef) {
        super(IdentityFreeConsumer.explicit((DeclarativeComponentContext ctx) -> {
            final DeclarativeComponentWrapper<R> self = Objects.requireNonNull(
                    selfRef.get(),
                    "Body invoked before wrapper could set a reference to itself");
            self.invokeWrappedBody(ctx);
        }, body));
        this.wrapperBody = body;
        selfRef.set(this);
    }

    private void invokeWrappedBody(DeclarativeComponentContext ctx) {
        if(wasDeepUpdated && !isDeepUpdated)
            throw new UnsupportedOperationException("Wrapped component missed an update");

        prevWrapped = wrapped;
        wrapped = wrapperBody.apply(ctx).doApplyInternal();
        wasDeepUpdated = isDeepUpdated;
        isDeepUpdated = false;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void substitute(StatefulDeclarativeComponent<?, ?, ?> other0) {
        super.substitute(other0);

        final DeclarativeComponentWrapper<R> other = (DeclarativeComponentWrapper<R>) other0;
        isDeepUpdated = other.isDeepUpdated;
        wasDeepUpdated = other.wasDeepUpdated;
        wrapped = other.wrapped;
        prevWrapped = other.prevWrapped;
    }

    @Override
    protected StatefulContext newContext() {
        return new StatefulContext(this);
    }

    @Override
    protected void copyContext(@Nullable StatefulContext toCopy) {
        context = toCopy == null ? null : new StatefulContext(this, toCopy);
    }

    @Override
    public String getDeclarativeType() {
        return wrapperBody.getImplClass().getName();
    }

    @Override
    public R getComponent() {
        final StatefulDeclarativeComponent<? extends R, ?, ?> wrapped = Objects.requireNonNull(
                this.wrapped,
                "getComponent(...) attribute called without having invoked the wrapper body");
        return wrapped.getComponent();
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
        // We do not want to eagerly execute component updates as there are possibly other updates
        // already scheduled in the framework specific scheduler, and we should respect that order,
        // so try to schedule stuff to be executed on said framework scheduler

        StatefulDeclarativeComponent<?, ?, ?> comp = wrapped;
        // Special case for nested wrappers, go on until we get to an actual component
        while (comp instanceof DeclarativeComponentWrapper)
            comp = ((DeclarativeComponentWrapper<?>) comp).wrapped;

        if(comp != null) {
            comp.scheduleOnFrameworkThread(priority, runnable);
        } else {
            runnable.run();
        }
    }

    @Override
    public void runOrScheduleOnFrameworkThread(Runnable runnable) {
        // We do not want to eagerly execute component updates as there are possibly other updates
        // already scheduled in the framework specific scheduler, and we should respect that order,
        // so try to schedule stuff to be executed on said framework scheduler

        StatefulDeclarativeComponent<?, ?, ?> comp = wrapped;
        // Special case for nested wrappers, go on until we get to an actual component
        while (comp instanceof DeclarativeComponentWrapper)
            comp = ((DeclarativeComponentWrapper<?>) comp).wrapped;

        if(comp != null) {
            comp.runOrScheduleOnFrameworkThread(runnable);
        } else {
            runnable.run();
        }
    }

    @Override
    protected void updateAttributes(StatefulContext newCtx) {
        final StatefulDeclarativeComponent<? extends R, ?, ?> curr = Objects.requireNonNull(
                wrapped,
                "updateAttributes(...) called without having invoked the wrapper body");

        this.isDeepUpdated = true;
        FnAttribute.updateDeclarativeComponent(
                getAppConfig(),
                lookups(),
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
