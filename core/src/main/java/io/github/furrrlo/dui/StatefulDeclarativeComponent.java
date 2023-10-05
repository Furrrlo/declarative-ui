package io.github.furrrlo.dui;

import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.*;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract class StatefulDeclarativeComponent<
        T,
        R,
        O_CTX extends DeclarativeComponentContext<T>,
        I_CTX extends StatefulDeclarativeComponent.StatefulContext<T>> implements DeclarativeComponent<R> {

    private static final Logger LOGGER = Logger.getLogger(StatefulDeclarativeComponent.class.getName());

    private static final ThreadLocal<StatefulDeclarativeComponent<?, ?, ?, ?>> CURR_UPDATING_COMPONENT =
            ThreadLocal.withInitial(() -> null);

    protected final @Nullable Body<T, O_CTX> body;

    protected AtomicReference<StatefulDeclarativeComponent<T, R, O_CTX, I_CTX>> substituteComponentRef = new AtomicReference<>(this);
    protected List<Memoized<?>> memoizedVars = new ArrayList<>();
    protected I_CTX context;
    protected boolean isInvokingBody;
    protected @Nullable IdentifiableRunnable currentStateDependency;

    public StatefulDeclarativeComponent(@Nullable Body<T, O_CTX> body) {
        this.body = body;
    }

    @SuppressWarnings("unchecked")
    protected void copy(StatefulDeclarativeComponent<?, ?, ?, ?> other0) {
        ensureSame("type", other0, StatefulDeclarativeComponent::getClass);

        final StatefulDeclarativeComponent<T, R, O_CTX, I_CTX> other = (StatefulDeclarativeComponent<T, R, O_CTX, I_CTX>) other0;
        substituteComponentRef = other.substituteComponentRef;
        substituteComponentRef.set(this);
        memoizedVars = other.memoizedVars;
        copyContext(other.context);
    }

    @SuppressWarnings("unchecked")
    protected <SELF> void ensureSame(String name,
                                     SELF other,
                                     Function<SELF, Object> fieldExtractor) {
        final Object ourField = fieldExtractor.apply((SELF) this);
        final Object otherField = fieldExtractor.apply(other);

        if (!Objects.equals(ourField, otherField))
            throw new UnsupportedOperationException("" +
                    "DeclarativeComponent " + name + " changed across re-renders for " + other + ":" +
                    "was " + ourField + ", is " + otherField);
    }

    protected abstract I_CTX newContext();

    protected abstract void copyContext(@Nullable I_CTX toCopy);

    public abstract @Nullable String getDeclarativeType();

    public abstract R getComponent();

    public R updateOrCreateComponent() {
        updateComponent();
        return getComponent();
    }

    public abstract void triggerComponentUpdate();

    public abstract void scheduleOnFrameworkThread(Runnable runnable);

    public abstract void runOrScheduleOnFrameworkThread(Runnable runnable);

    protected void updateComponent() {
        updateComponent(true);
    }

    protected void runAsComponentUpdate(Runnable runnable) {
        final StatefulDeclarativeComponent<?, ?, ?, ?> prevUpdatingComponent = CURR_UPDATING_COMPONENT.get();
        CURR_UPDATING_COMPONENT.set(this);
        try {
            runnable.run();
        } finally {
            if(prevUpdatingComponent == null)
                CURR_UPDATING_COMPONENT.remove();
            else
                CURR_UPDATING_COMPONENT.set(prevUpdatingComponent);
        }
    }

    @SuppressWarnings("unchecked")
    protected void updateComponent(boolean deepUpdate) {
        if(substituteComponentRef.get() != this)
            throw new UnsupportedOperationException("Trying to update substituted component");

        runAsComponentUpdate(() -> {
            final I_CTX newCtx = newContext();
            if (body != null) {
                isInvokingBody = true;
                // This cast to C has to be guaranteed by the DeclarativeComponentFactory
                invokeBody(body, (O_CTX) newCtx);
                isInvokingBody = false;
            }

            if (context != null && newCtx.getCurrMemoizedIdx() != context.getCurrMemoizedIdx())
                throw new UnsupportedOperationException("Memoized variables differ across re-renders, " +
                        "did you put any state/memo in conditionals?" +
                        " before " + newCtx.getCurrMemoizedIdx() + ", " +
                        "after" + newCtx.getCurrMemoizedIdx());

            if (deepUpdate)
                updateAttributes(newCtx);
            context = newCtx;
        });
    }

    protected void invokeBody(Body<T, O_CTX> body, O_CTX newCtx) {
        body.component(newCtx);
    }

    protected void updateAttributes(I_CTX newCtx) {
    }

    protected @Nullable IdentifiableRunnable getCurrentStateDependency() {
        return currentStateDependency != null
                ? currentStateDependency
                : makeStateDependency(StatefulDeclarativeComponent::triggerComponentUpdate, c -> new Object[] { c });
    }

    @SuppressWarnings("unchecked")
    public <C extends StatefulDeclarativeComponent<?, ?, ?, ?>> IdentifiableRunnable makeStateDependency(
            Consumer<C> runnable,
            Function<C, Object[]> deps) {
        Supplier<StatefulDeclarativeComponent<T, R, O_CTX, I_CTX>> componentRef = substituteComponentRef::get;
        return IdentifiableRunnable.of(
                () -> runnable.accept((C) componentRef.get()),
                () -> deps.apply((C) componentRef.get()));
    }

    @SuppressWarnings("unchecked")
    public <V, M extends Memoized<V>> IdentifiableRunnable makeMemoStateDependency(
            int memoIdx,
            BiConsumer<StatefulDeclarativeComponent<T, R, O_CTX, I_CTX>, M> runnable,
            BiFunction<StatefulDeclarativeComponent<T, R, O_CTX, I_CTX>, M, Object[]> deps) {
        return this.<StatefulDeclarativeComponent<T, R, O_CTX, I_CTX>>makeStateDependency(
                c -> {
                    final Memoized<?> memo;
                    if(memoIdx < c.memoizedVars.size() && (memo = c.memoizedVars.get(memoIdx)) != null)
                        runnable.accept(c, (M) memo);
                },
                c -> {
                    final Memoized<?> memo;
                    if(memoIdx < c.memoizedVars.size() && (memo = c.memoizedVars.get(memoIdx)) != null)
                        return deps.apply(c, (M) memo);
                    return new Object[] { c, memoIdx };
                });
    }

    private <RET, V, M extends Memoized<V>> RET updateMemoWithStateDependency(int memoIdx, Supplier<RET> factory) {
        IdentifiableRunnable prevStateDependency = currentStateDependency;
        // Notice how it's not capturing neither this nor attr, as both  might be replaced with
        // newer versions, and we do not want to update stale stuff
        this.currentStateDependency = this.<V, M>makeMemoStateDependency(
                memoIdx,
                (c, memo) -> {
                    // Mark the memo to be updated, so if for any reason its parent component is scheduled
                    // before this, and therefore it's re-run before we can get to the update scheduled below,
                    // the memo is updated right away (and not on the schedule below)
                    memo.markForUpdate();
                    // Schedule an update
                    c.scheduleOnFrameworkThread(() -> {
                        // If when we get here the memo was not already updated, do it now
                        if(memo.isMarkedForUpdate())
                            c.runAsComponentUpdate(() -> c.updateMemoWithStateDependency(memoIdx, () -> {
                                memo.update();
                                return null;
                            }));
                    });
                },
                (c, memo) -> new Object[] { memo });
        try {
            return factory.get();
        } finally {
            this.currentStateDependency = prevStateDependency;
        }
    }

    protected void disposeComponent() {
    }

    @Override
    public String toString() {
        return "StatefulDeclarativeComponent{" +
                "body=" + body +
                ", memoizedVars=" + memoizedVars +
                ", context=" + context +
                ", isInvokingBody=" + isInvokingBody +
                '}';
    }

    protected static class StatefulContext<T> implements DeclarativeComponentContext<T> {

        private final StatefulDeclarativeComponent<T, ?, ?, ?> outer;
        private int currMemoizedIdx;

        public StatefulContext(StatefulDeclarativeComponent<T, ?, ?, ?> outer) {
            this.outer = outer;
            this.currMemoizedIdx = 0;
        }

        public StatefulContext(StatefulDeclarativeComponent<T, ?, ?, ?> outer,
                               StatefulContext<T> other) {
            this.outer = outer;
            this.currMemoizedIdx = other.currMemoizedIdx;
        }

        protected int getCurrMemoizedIdx() {
            return currMemoizedIdx;
        }

        protected void ensureInsideBody() {
            if(!outer.isInvokingBody)
                throw new UnsupportedOperationException("Invalid state/attribute invocation, can only be done inside body");
        }

        @Override
        public <V> State<V> useState(V value) {
            ensureInsideBody();
            return useState(() -> value);
        }

        @Override
        public <V> State<V> useState(Supplier<V> value) {
            Memoized<State<V>> memo = useMemo(IdentifiableSupplier.explicit(() -> new StateImpl<>(value.get())));
            return memo.value; // Access directly to avoid setting a signal dependency by calling get()
        }

        @Override
        @SuppressWarnings("unchecked")
        public <V> Memoized<V> useMemo(IdentifiableSupplier<V> value) {
            ensureInsideBody();

            final List<Object> dependencies = Arrays.asList(value.deps());
            if(currMemoizedIdx < outer.memoizedVars.size()) {
                final int idx = currMemoizedIdx++;
                final Memoized<V> memo = (Memoized<V>) outer.memoizedVars.get(idx);
                return outer.updateMemoWithStateDependency(idx, () -> memo.updateIfNecessary(value, dependencies));
            }

            final Memoized<V> newMemo = outer.updateMemoWithStateDependency(currMemoizedIdx,
                    () -> new Memoized<>(value, dependencies));
            if(LOGGER.isLoggable(Level.FINE))
                LOGGER.log(Level.FINE, "Created memoized value ({0}) {1} for {2}",
                        new Object[] { currMemoizedIdx, newMemo.value, newMemo.dependencies });
            outer.memoizedVars.add(newMemo);
            currMemoizedIdx++;
            return newMemo;
        }

        @Override
        public <V> DeclarativeComponentContext<T> inner(Function<T, V> getter, DeclarativeComponent<V> component) {
            ensureInsideBody();
            return this;
        }

        @Override
        public <V> DeclarativeComponentContext<T> attribute(String key, BiConsumer<T, V> setter, Supplier<V> value) {
            ensureInsideBody();
            return this;
        }

        @Override
        public <V, S extends DeclarativeComponentWithIdSupplier<? extends V>> DeclarativeComponentContext<T> listAttribute(
                String key,
                Class<V> type,
                ListReplacer<T, V, S> replacer,
                List<V> fn
        ) {
            ensureInsideBody();
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
            ensureInsideBody();
            return this;
        }

        @Override
        public <C1> DeclarativeComponentContext<T> fnAttribute(String key, BiConsumer<T, C1> setter, DeclarativeComponentSupplier<C1> fn) {
            ensureInsideBody();
            return this;
        }

        @Override
        public <C, S extends DeclarativeComponentWithIdSupplier<? extends C>> DeclarativeComponentContext<T> listFnAttribute(
                String key,
                ListSetter<T, C, S> setter,
                List<S> fn
        ) {
            ensureInsideBody();
            return this;
        }

        @Override
        public <C, S extends DeclarativeComponentWithIdSupplier<? extends C>> DeclarativeComponentContext<T> listFnAttribute(
                String key,
                ListReplacer<T, C, S> replacer,
                List<S> fn
        ) {
            ensureInsideBody();
            return this;
        }

        @Override
        public <C1, S extends DeclarativeComponentWithIdSupplier<? extends C1>> DeclarativeComponentContext<T> listFnAttribute(
                String key,
                ListAdder<T, C1, S> adder,
                ListRemover<T> remover,
                List<S> fn
        ) {
            ensureInsideBody();
            return this;
        }
    }

    private static class BaseMemo<V> implements Memo<V> {

        private final Set<Runnable> signalDeps = new LinkedHashSet<>();

        protected V value;

        @Override
        public V get() {
            final StatefulDeclarativeComponent<?, ?, ?, ?> currUpdatingComponent = CURR_UPDATING_COMPONENT.get();
            if(currUpdatingComponent == null) {
                CURR_UPDATING_COMPONENT.remove();
            } else {
                Runnable currDependency = currUpdatingComponent.getCurrentStateDependency();
                if (currDependency != null)
                    signalDeps.add(currDependency);
            }

            return value;
        }

        protected void set(V value) {
            this.value = value;

            Set<Runnable> dependencies = new LinkedHashSet<>(this.signalDeps);
            this.signalDeps.clear();
            dependencies.forEach(Runnable::run);
        }
    }

    private static class Memoized<V> extends BaseMemo<V> {

        private Supplier<V> supplier;
        private boolean markedForUpdate;
        private List<Object> dependencies;

        public Memoized(Supplier<V> supplier, List<Object> dependencies) {
            this.value = supplier.get();
            this.supplier = supplier;
            this.dependencies = dependencies;
        }

        public void markForUpdate() {
            markedForUpdate = true;
        }

        public boolean isMarkedForUpdate() {
            return markedForUpdate;
        }

        public void update() {
            set(supplier.get());

            if(LOGGER.isLoggable(Level.FINE))
                LOGGER.log(Level.FINE, "Updated memoized value {0} (deps: {1} -> {2})",
                        new Object[] { value, this.dependencies, dependencies });

            markedForUpdate = false;
        }

        public Memoized<V> updateIfNecessary(Supplier<V> newValue, List<Object> dependencies) {
            if(markedForUpdate || !this.dependencies.equals(dependencies)) {
                this.supplier = newValue;
                this.dependencies = dependencies;
                update();
            }
            return this;
        }
    }

    private static class StateImpl<S> extends BaseMemo<S> implements State<S> {

        public StateImpl(S value) {
            this.value = value;
        }

        @Override
        public void set(S value) {
            super.set(value);
        }

        @Override
        public S update(Function<S, S> updater) {
            set(updater.apply(get()));
            return get();
        }
    }
}
