package io.github.furrrlo.dui;

import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

class DeclarativeComponentWrapper<R> extends StatefulDeclarativeComponent<
        Object, R, DeclarativeComponentContext<Object>, StatefulDeclarativeComponent.StatefulContext<Object>> {


    private final Function<DeclarativeComponentContext<?>, DeclarativeComponentSupplier<R>> wrapperBody;
    private final boolean hasDeps;
    private final List<Object> newDeps;

    private boolean wasDeepUpdated;
    private @Nullable List<Object> deps;
    private @Nullable List<Object> prevDeps;
    private @Nullable StatefulDeclarativeComponent<?, R, ?, ?> wrapped;
    private @Nullable StatefulDeclarativeComponent<?, R, ?, ?> prevWrapped;

    public DeclarativeComponentWrapper(List<Object> deps,
                                       Function<DeclarativeComponentContext<?>, DeclarativeComponentSupplier<R>> body) {
        this(true, deps, body, new AtomicReference<>());
    }

    public DeclarativeComponentWrapper(Function<DeclarativeComponentContext<?>, DeclarativeComponentSupplier<R>> body) {
        this(false, Collections.emptyList(), body, new AtomicReference<>());
    }

    private DeclarativeComponentWrapper(boolean hasDeps,
                                        List<Object> deps,
                                        Function<DeclarativeComponentContext<?>, DeclarativeComponentSupplier<R>> body,
                                        AtomicReference<DeclarativeComponentWrapper<R>> selfRef) {
        super(ctx -> {
            final DeclarativeComponentWrapper<R> self = Objects.requireNonNull(
                    selfRef.get(),
                    "Body invoked before wrapper could set a reference to itself");
            self.invokeWrappedBody(ctx);
        });
        this.wrapperBody = body;
        this.hasDeps = hasDeps;
        this.newDeps = deps;
        selfRef.set(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void copy(StatefulDeclarativeComponent<?, ?, ?, ?> other0) {
        super.copy(other0);

        final DeclarativeComponentWrapper<R> other = (DeclarativeComponentWrapper<R>) other0;
        ensureSame("hasDeps", other, f -> f.hasDeps);

        wasDeepUpdated = other.wasDeepUpdated;
        deps = other.deps;
        prevDeps = other.prevDeps;
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

    @Override
    public String getDeclarativeType() {
        final StatefulDeclarativeComponent<?, R, ?, ?> wrapped = Objects.requireNonNull(
                this.wrapped,
                "getDeclarativeType() attribute called without having invoked the wrapper body");
        return wrapperBody.getClass().getName() + "[" + wrapped.getDeclarativeType() + "]";
    }

    @Override
    protected void updateComponent(boolean deepUpdate) {
        super.updateComponent(deepUpdate);
        // Wrappers need to invoke their body before they can say declarativeType
        // so if this was soft-updated, propagate the update to the child wrapper
        if(!deepUpdate && wrapped instanceof DeclarativeComponentWrapper) {
            if(prevWrapped != null)
                wrapped.copy(prevWrapped);
            wrapped.updateComponent(false);
        }
    }

    private void invokeWrappedBody(DeclarativeComponentContext<Object> ctx) {
        final boolean depsChanged = !hasDeps || !newDeps.equals(deps);
        if(depsChanged) {
            prevWrapped = wrapped;
            wrapped = wrapperBody.apply(ctx).doApplyInternal();
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
    public void triggerComponentUpdate() {
        updateComponent(true);
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

        final boolean depsChanged = !hasDeps || !newDeps.equals(deps);
        prevDeps = deps;
        deps = newDeps;

        if(!depsChanged)
            return;

        final boolean wasDeepUpdated = this.wasDeepUpdated;
        this.wasDeepUpdated = true;
        Attribute.updateDeclarativeComponent(
                wasDeepUpdated,
                curr,
                prevWrapped,
                null,
                null);
    }
}
