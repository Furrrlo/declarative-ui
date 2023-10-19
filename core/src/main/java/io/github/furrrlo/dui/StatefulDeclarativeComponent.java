package io.github.furrrlo.dui;

import io.github.furrrlo.dui.DeclarativeComponentContextDecorator.ReservedMemoProxy;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

abstract class StatefulDeclarativeComponent<
        T,
        R,
        O_CTX extends DeclarativeComponentContext<T>,
        I_CTX extends StatefulDeclarativeComponent.StatefulContext<T>> implements DeclarativeComponent<R> {

    private static final Logger LOGGER = Logger.getLogger(StatefulDeclarativeComponent.class.getName());

    private static final ThreadLocal<StatefulDeclarativeComponent<?, ?, ?, ?>> CURR_UPDATING_COMPONENT =
            ThreadLocal.withInitial(() -> null);

    protected final @Nullable IdentifiableConsumer<O_CTX> body;
    protected final List<Object> newDeps;
    private @Nullable List<Object> deps;

    protected AtomicReference<@Nullable StatefulDeclarativeComponent<T, R, O_CTX, I_CTX>> substituteComponentRef =
            new AtomicReference<>(this);
    protected List<Memoized<?>> memoizedVars = new ArrayList<>();
    protected I_CTX context;

    protected boolean isInvokingBody;
    protected @Nullable IdentifiableRunnable currentStateDependency;

    protected StatefulDeclarativeComponent(@Nullable IdentifiableConsumer<O_CTX> body) {
        this.newDeps = body != null ? Arrays.asList(body.deps()) : Collections.emptyList();
        this.body = body;
    }

    @SuppressWarnings("unchecked")
    protected void substitute(StatefulDeclarativeComponent<?, ?, ?, ?> other0) {
        ensureSame("type", other0, StatefulDeclarativeComponent::getClass);

        final StatefulDeclarativeComponent<T, R, O_CTX, I_CTX> other = (StatefulDeclarativeComponent<T, R, O_CTX, I_CTX>) other0;
        substituteComponentRef = other.substituteComponentRef;
        if(substituteComponentRef.get() == null)
            throw new UnsupportedOperationException("Trying to resuscitate a disposed component");
        substituteComponentRef.set(this);
        deps = other.deps;
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

    public abstract void triggerStateUpdate();

    public abstract void scheduleOnFrameworkThread(Runnable runnable);

    public abstract void runOrScheduleOnFrameworkThread(Runnable runnable);

    protected void runAsComponentUpdate(Runnable runnable) {
        if(substituteComponentRef.get() != this)
            throw new UnsupportedOperationException("Trying to update substituted component");

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

    static class UpdateFlags {
        public static final int SOFT = 0x1;
        public static final int FORCE = 0x2;
    }

    protected void updateComponent() {
        updateComponent(0);
    }

    @SuppressWarnings("unchecked")
    protected void updateComponent(int flags) {
        runAsComponentUpdate(() -> {
            final boolean deepUpdate = (flags & UpdateFlags.SOFT) == 0;
            final boolean depsChanged = (flags & UpdateFlags.FORCE) != 0 || !newDeps.equals(deps);

            final I_CTX newCtx;
            if(!depsChanged) {
                newCtx = context;
            } else {
                newCtx = newContext();
                if (body != null) {
                    isInvokingBody = true;
                    // This cast to O_CTX has to be guaranteed by the DeclarativeComponentFactory
                    invokeBody(body, newCtx, (O_CTX) newCtx);
                    isInvokingBody = false;
                }
            }

            try {
                if (context != null && newCtx.getCurrMemoizedIdx() != context.getCurrMemoizedIdx())
                    throw new UnsupportedOperationException("Memoized variables differ across re-renders " +
                            "for component " + getDeclarativeType() + ", " +
                            "did you put any state/memo in conditionals?" +
                            " before " + context.getCurrMemoizedIdx() + ", " +
                            "after " + newCtx.getCurrMemoizedIdx());

                if (deepUpdate) {
                    // We only register the deps change if we are deep updating, otherwise
                    // if we have a soft update followed by a deep update, we would in practise
                    // skip the deep update also on the second call, as the first would consume
                    // the deps change
                    deps = newDeps;

                    if (depsChanged)
                        updateAttributes(newCtx);
                }
                context = newCtx;
            } catch (Throwable t) {
                Throwable bodyStackTrace = newCtx.getCapturedBodyStacktrace();
                if(bodyStackTrace != null)
                    t.addSuppressed(bodyStackTrace);
                throw t;
            }
        });
    }

    protected void invokeBody(IdentifiableConsumer<O_CTX> body, I_CTX underlyingNewCtx, O_CTX newCtx) {
        body.accept(newCtx);
    }

    protected void updateAttributes(I_CTX newCtx) {
    }

    protected @Nullable IdentifiableRunnable getCurrentStateDependency() {
        return currentStateDependency != null
                ? currentStateDependency
                : makeStateDependency(StatefulDeclarativeComponent::triggerStateUpdate, c -> new Object[] { c });
    }

    @SuppressWarnings("unchecked")
    public <C extends StatefulDeclarativeComponent<?, ?, ?, ?>> IdentifiableRunnable makeStateDependency(
            Consumer<C> runnable,
            Function<C, Object[]> deps) {
        Supplier<StatefulDeclarativeComponent<T, R, O_CTX, I_CTX>> componentRef = substituteComponentRef::get;
        return IdentifiableRunnable.of(
                () -> {
                    C sub = (C) componentRef.get();
                    if(sub != null)
                        runnable.accept(sub);
                },
                () -> {
                    C sub = (C) componentRef.get();
                    return sub != null ? deps.apply((C) componentRef.get()) : new Object[] { componentRef };
                });
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
                        if(!memo.isMarkedForUpdate())
                            return;
                        // If when we get here the memo was not already updated, do it now
                        final StatefulDeclarativeComponent<T, R, O_CTX, I_CTX> sub = c.substituteComponentRef.get();
                        if(sub == null)
                            return;
                        // Even if the component was substituted, memos are shallowly passed to the new one
                        // so no need to search back for it in the context
                        sub.runAsComponentUpdate(() -> sub.updateMemoWithStateDependency(memoIdx, () -> {
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

    private <RET> RET updateWithWrappedStateDependency(Predicate<StatefulDeclarativeComponent<?, ?, ?, ?>> wrappingCond,
                                                       Supplier<RET> factory) {
        IdentifiableRunnable prevStateDependency = currentStateDependency;
        IdentifiableRunnable wrappedStateDependency = getCurrentStateDependency();
        if(wrappedStateDependency != null)
            this.currentStateDependency = this.makeStateDependency(
                    c -> {
                        if(wrappingCond.test(c))
                            wrappedStateDependency.run();
                    },
                    c -> wrappedStateDependency.deps());
        try {
            return factory.get();
        } finally {
            this.currentStateDependency = prevStateDependency;
        }
    }

    protected void disposeComponent() {
        substituteComponentRef.set(null);
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

        private static final Throwable STACKTRACE_SENTINEL = new Exception("StatefulContext sentinel");
        private static final String DUI_PACKAGE;
        static {
            String name = StatefulDeclarativeComponent.class.getName();
            DUI_PACKAGE = name.substring(0, name.lastIndexOf("."));
        }

        private final StatefulDeclarativeComponent<T, ?, ?, ?> outer;
        private int currMemoizedIdx;
        private @Nullable Throwable capturedBodyStacktrace;

        public StatefulContext(StatefulDeclarativeComponent<T, ?, ?, ?> outer) {
            this.outer = outer;
            this.currMemoizedIdx = 0;
        }

        public StatefulContext(StatefulDeclarativeComponent<T, ?, ?, ?> outer,
                               StatefulContext<T> other) {
            this.outer = outer;
            this.currMemoizedIdx = other.currMemoizedIdx;
            this.capturedBodyStacktrace = other.capturedBodyStacktrace;
        }

        protected int getCurrMemoizedIdx() {
            return currMemoizedIdx;
        }

        protected @Nullable Throwable getCapturedBodyStacktrace() {
            return capturedBodyStacktrace != STACKTRACE_SENTINEL ? capturedBodyStacktrace : null;
        }

        protected void ensureInsideBody() {
            // TODO: currently, memos are evaluated immediately inside the body, so
            //       using useMemo inside a memo does not fail fast, as it should
            if(!outer.isInvokingBody)
                throw new UnsupportedOperationException("Invalid state/attribute invocation, can only be done inside body");

            if(capturedBodyStacktrace == null) {
                Throwable t = new Exception("Body stack frame");
                capturedBodyStacktrace = Arrays.stream(t.getStackTrace())
                        .filter(el -> !el.getClassName().startsWith(DUI_PACKAGE))
                        .findFirst()
                        .map(el -> {
                            t.setStackTrace(new StackTraceElement[] { el });
                            return t;
                        })
                        .orElse(STACKTRACE_SENTINEL);
            }
        }

        @Override
        public <V> State<V> useState(V value, BiPredicate<V, V> equalityFn) {
            ensureInsideBody();
            return this.<V>useState(() -> value, equalityFn);
        }

        @Override
        public <V> State<V> useState(Supplier<V> value, BiPredicate<V, V> equalityFn) {
            Memoized<State<V>> memo = useMemo(
                    IdentifiableSupplier.explicit(() -> new StateImpl<>(value.get(), equalityFn)),
                    (prev, next) -> false);
            return memo.value; // Access directly to avoid setting a signal dependency by calling get()
        }

        @Override
        public <V> Memoized<V> useMemo(IdentifiableSupplier<V> value, BiPredicate<V, V> equalityFn) {
            ensureInsideBody();

            // Try to catch memo issues as soon as possible from within the component
            // body so that the stacktrace is more helpful
            if (outer.context != null && getCurrMemoizedIdx() > outer.context.getCurrMemoizedIdx())
                throw new UnsupportedOperationException("Memoized variables increased in this rerender, " +
                        "did you put any state/memo in conditionals?" +
                        " before " + getCurrMemoizedIdx() + ", " +
                        "now" + outer.context.getCurrMemoizedIdx());

            final int idx = currMemoizedIdx++;
            return doUseMemo(idx, value, equalityFn);
        }

        protected <V> void reserveMemo(ReservedMemoProxy<V> reservedMemoProxy) {
            final int idx = currMemoizedIdx++;
            final BiPredicate<V, V> equalityFn = reservedMemoProxy.getEqualityFn();
            reservedMemoProxy.setReservedMemo(fn -> doUseMemo(idx, fn, equalityFn));
        }

        @SuppressWarnings("unchecked")
        protected <V> Memoized<V> doUseMemo(int index, IdentifiableSupplier<V> value, BiPredicate<V, V> equalityFn) {
            final List<Object> dependencies = Arrays.asList(value.deps());
            if(index < outer.memoizedVars.size() && outer.memoizedVars.get(index) != null) {
                final Memoized<V> memo = (Memoized<V>) outer.memoizedVars.get(index);
                return outer.updateMemoWithStateDependency(index, () -> memo.updateIfNecessary(value, dependencies));
            }

            final Memoized<V> newMemo = outer.updateMemoWithStateDependency(index,
                    () -> new Memoized<>(value, dependencies, equalityFn));
            if(LOGGER.isLoggable(Level.FINE))
                LOGGER.log(Level.FINE, "Created memoized value ({0}) {1} for {2}",
                        new Object[] { index, newMemo.value, newMemo.dependencies });
            if(index < outer.memoizedVars.size()) {
                outer.memoizedVars.set(index, newMemo);
            } else {
                // There might be reserved values before, so fill missing spots with nulls
                while (outer.memoizedVars.size() < index)
                    outer.memoizedVars.add(null);
                outer.memoizedVars.add(newMemo);
            }
            return newMemo;
        }

        @Override
        public <V, R> List<DeclarativeComponent<R>> indexCollection(
                IdentifiableSupplier<Collection<V>> collection,
                IdentifiableBiFunction<Memo<V>, Integer, DeclarativeComponentSupplier<R>> mapFn) {

            final AtomicReference<Integer> previousSize = new AtomicReference<>();
            return outer.updateWithWrappedStateDependency(
                    c -> {
                        // If the size changed from the last time we ran, we want to re-run the
                        // entire block anyway (either the whole component update or an attribute
                        // update)
                        final int currSize = collection.get().size();
                        final Integer prev = previousSize.get();
                        return prev == null || prev != currSize;
                    },
                    () -> {
                        // We can access the collection here as we have set up a proper dependency
                        // which will only fire updates if the size has changed
                        int size = collection.get().size();
                        previousSize.set(size);
                        return IntStream.range(0, size).mapToObj(i -> DWrapper.fn(IdentifiableFunction.explicit(wrapper -> {
                            final Memo<V> memo = wrapper.useMemo(IdentifiableSupplier.explicit(
                                    () -> {
                                        // If the collection changes this will be re-evaluated
                                        Collection<V> coll = collection.get();
                                        if(coll instanceof List<?>)
                                            return ((List<V>) coll).get(i);
                                        return coll.stream().skip(i).findFirst().orElseThrow(IndexOutOfBoundsException::new);
                                    },
                                    () -> {
                                        final Object[] listDeps = collection.deps();
                                        final Object[] memoDeps = Arrays.copyOf(listDeps, listDeps.length + 1);
                                        memoDeps[memoDeps.length - 1] = i;
                                        return memoDeps;
                                    }));
                            return mapFn.apply(memo, i);
                        }, () -> {
                            final Object[] listDeps = collection.deps();
                            final Object[] mapFnDeps = mapFn.deps();
                            final Object[] memoDeps = Arrays.copyOf(listDeps, listDeps.length + mapFnDeps.length + 1);
                            System.arraycopy(mapFnDeps, 0, memoDeps, listDeps.length, mapFnDeps.length);
                            memoDeps[memoDeps.length - 1] = i;
                            return memoDeps;
                        }))).collect(Collectors.toList());
                    });
        }

        @Override
        public <V> DeclarativeComponentContext<T> inner(Function<T, V> getter, DeclarativeComponent<V> component) {
            ensureInsideBody();
            return this;
        }

        @Override
        public <V> DeclarativeComponentContext<T> attribute(String key,
                                                            BiConsumer<T, V> setter,
                                                            Supplier<V> value,
                                                            AttributeEqualityFn<T, V> equalityFn) {
            ensureInsideBody();
            return this;
        }

        @Override
        public <V, S extends DeclarativeComponentWithIdSupplier<? extends V>> DeclarativeComponentContext<T> listAttribute(
                String key,
                Class<V> type,
                ListReplacer<T, V, S> replacer,
                Supplier<List<V>> fn
        ) {
            ensureInsideBody();
            return this;
        }

        @Override
        public <V, S extends DeclarativeComponentWithIdSupplier<? extends V>> DeclarativeComponentContext<T> listAttribute(
                String key,
                Class<V> type,
                ListRemover<T> remover,
                Supplier<List<V>> fn,
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
                Supplier<List<S>> fn
        ) {
            ensureInsideBody();
            return this;
        }

        @Override
        public <C, S extends DeclarativeComponentWithIdSupplier<? extends C>> DeclarativeComponentContext<T> listFnAttribute(
                String key,
                ListReplacer<T, C, S> replacer,
                Supplier<List<S>> fn
        ) {
            ensureInsideBody();
            return this;
        }

        @Override
        public <C1, S extends DeclarativeComponentWithIdSupplier<? extends C1>> DeclarativeComponentContext<T> listFnAttribute(
                String key,
                ListAdder<T, C1, S> adder,
                ListRemover<T> remover,
                Supplier<List<S>> fn
        ) {
            ensureInsideBody();
            return this;
        }
    }

    private static class BaseMemo<V> implements Memo<V> {

        private final Set<Runnable> signalDeps = new LinkedHashSet<>();

        private final BiPredicate<V, V> equalityFn;
        protected V value;

        public BaseMemo(BiPredicate<V, V> equalityFn) {
            this.equalityFn = equalityFn;
        }

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
            if(equalityFn.test(this.value, value))
                return;

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

        public Memoized(Supplier<V> supplier, List<Object> dependencies, BiPredicate<V, V> equalityFn) {
            super(equalityFn);
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

        public StateImpl(S value, BiPredicate<S, S> equalityFn) {
            super(equalityFn);
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
