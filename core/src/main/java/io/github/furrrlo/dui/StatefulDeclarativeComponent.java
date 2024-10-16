package io.github.furrrlo.dui;

import io.github.furrrlo.dui.DeclarativeComponentContextDecorator.ReservedMemoProxy;
import io.github.furrrlo.dui.Hooks.DisposableEffectScope;
import org.jspecify.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.github.furrrlo.dui.Hooks.useMemo;

abstract class StatefulDeclarativeComponent<
        R,
        O_CTX extends DeclarativeComponentContext,
        I_CTX extends StatefulDeclarativeComponent.StatefulContext> implements DeclarativeComponent<R> {

    private static final Logger LOGGER = Logger.getLogger(StatefulDeclarativeComponent.class.getName());

    protected static final int COMPONENT_UPDATE_PRIORITY = 0;
    protected static final int MEMO_UPDATE_PRIORITY = 1;
    protected static final int SUBCOMPONENT_ATTRIBUTE_UPDATE_PRIORITY = 2;
    protected static final int NORMAL_ATTRIBUTE_UPDATE_PRIORITY = 3;
    protected static final int EFFECT_UPDATE_PRIORITY = 4;
    protected static final int HIGHEST_PRIORITY = COMPONENT_UPDATE_PRIORITY;

    protected static final IdentityFreeRunnable NO_STATE_DEPENDENCY = IdentityFreeRunnable.explicit(() -> {});

    private static final DScopedValue<StatefulDeclarativeComponent<?, ?, ?>> CURR_UPDATING_COMPONENT = DScopedValue.create();

    private @Nullable ApplicationConfig appConfig;

    private final Function<Boolean, @Nullable IdentityFreeConsumer<O_CTX>> bodyFn;
    protected @Nullable IdentityFreeConsumer<O_CTX> prevBody;

    protected AtomicReference<@Nullable StatefulDeclarativeComponent<R, O_CTX, I_CTX>> substituteComponentRef =
            new AtomicReference<>(this);
    private List<Memoized<?>> memoizedVars = new ArrayList<>();
    private List<Effect> effects = new ArrayList<>();
    private Set<MethodHandles.Lookup> lookups = new LinkedHashSet<>();
    protected @Nullable I_CTX context;

    protected @Nullable DeclarativeComponentInternalContext currentBodyInvocationCtx;
    protected @Nullable IdentityFreeRunnable currentStateDependency;

    protected StatefulDeclarativeComponent(@Nullable IdentityFreeConsumer<O_CTX> body) {
        this(null, body);
    }

    protected StatefulDeclarativeComponent(@Nullable ApplicationConfig appConfig,
                                           @Nullable IdentityFreeConsumer<O_CTX> body) {
        this.bodyFn = new Function<Boolean, @Nullable IdentityFreeConsumer<O_CTX>>() {

            private @Nullable IdentityFreeConsumer<O_CTX> explicit;

            @Override
            public @Nullable IdentityFreeConsumer<O_CTX> apply(Boolean requiresExplicit) {
                if(body == null)
                    return null;
                if(explicit != null)
                    return explicit;

                if(requiresExplicit) {
                    IdentityFreeConsumer<O_CTX> explicit = IdentityFreeConsumer.explicit(currentLookups(), body);
                    this.explicit = explicit;
                    return explicit;
                }

                return body;
            }

            @Override
            public String toString() {
                return "BodyWrapper{" +
                        "explicit=" + (explicit != null) +
                        ", body=" + (explicit != null ? explicit : body) +
                        '}';
            }
        };
        this.appConfig = appConfig;
    }

    @SuppressWarnings("unchecked")
    protected void substitute(StatefulDeclarativeComponent<?, ?, ?> other0) {
        ensureSame("type", other0, StatefulDeclarativeComponent::getClass);

        final StatefulDeclarativeComponent<R, O_CTX, I_CTX> other = (StatefulDeclarativeComponent<R, O_CTX, I_CTX>) other0;
        substituteComponentRef = other.substituteComponentRef;
        if(substituteComponentRef.get() == null)
            throw new UnsupportedOperationException("Trying to resuscitate a disposed component");
        substituteComponentRef.set(this);
        appConfig = other.appConfig;
        prevBody = other.prevBody;
        memoizedVars = other.memoizedVars;
        effects = other.effects;
        lookups = other.lookups;
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

    public boolean hasBeenSubstituted() {
        return substituteComponentRef.get() == this;
    }

    protected @Nullable IdentityFreeConsumer<O_CTX> body(boolean requiresExplicit) {
        return bodyFn.apply(requiresExplicit);
    }

    protected abstract I_CTX newContext();

    protected abstract void copyContext(@Nullable I_CTX toCopy);

    public abstract @Nullable String getDeclarativeType();

    public abstract R getComponent();

    public ApplicationConfig getAppConfig() {
        return Objects.requireNonNull(appConfig, "App config was not set yet");
    }

    public R updateOrCreateComponent(ApplicationConfig appConfig, Collection<MethodHandles.Lookup> lookups) {
        this.appConfig = appConfig;
        this.lookups.addAll(lookups);
        updateComponent();
        return getComponent();
    }

    public abstract void triggerStateUpdate();

    public abstract void scheduleOnFrameworkThread(int priority, Runnable runnable);

    public abstract void runOrScheduleOnFrameworkThread(Runnable runnable);

    protected void runAsComponentUpdate(Runnable runnable) {
        if(substituteComponentRef.get() != this)
            throw new UnsupportedOperationException("Trying to update substituted component");

        CURR_UPDATING_COMPONENT.where(this, runnable);
    }

    static class UpdateFlags {
        public static final int SOFT = 0x1;
        public static final int FORCE = 0x2;
    }

    protected void updateComponent() {
        updateComponent(0);
    }

    protected void updateComponent(int flags) {
        runAsComponentUpdate(() -> {
            final boolean deepUpdate = (flags & UpdateFlags.SOFT) == 0;
            final boolean depsChanged = (flags & UpdateFlags.FORCE) != 0
                    || (prevBody == null && body(false) != null)
                    || !Objects.equals(body(true), prevBody);
            // If it's the first time that the body is being run, and it contains a grantAccess(...)
            // we may not be able to explicit-ize the body, but it shouldn't be needed anyway.
            // The previous depsChanged will only make it explicit if there was already a body, therefore
            // we would have the lookup to access the lambda from the previous time the body was run.
            IdentityFreeConsumer<O_CTX> body = body(false);

            final I_CTX newCtx;
            if(!depsChanged) {
                newCtx = context;
            } else {
                newCtx = newContext();
                if (body != null) {
                    currentBodyInvocationCtx = newCtx;
                    try {
                        invokeBody(body, newCtx, newCtx::reserveMemo);
                    } finally {
                        currentBodyInvocationCtx = null;
                    }
                }
            }

            try {
                if (context != null && newCtx.getCurrMemoizedIdx() != context.getCurrMemoizedIdx())
                    throw new UnsupportedOperationException("Memoized variables differ across re-renders " +
                            "for component " + getDeclarativeType() + ", " +
                            "did you put any state/memo in conditionals?" +
                            " before " + context.getCurrMemoizedIdx() + ", " +
                            "after " + newCtx.getCurrMemoizedIdx());
                if (context != null && newCtx.getCurrEffectsIdx() != context.getCurrEffectsIdx())
                    throw new UnsupportedOperationException("Effects differ across re-renders " +
                            "for component " + getDeclarativeType() + ", " +
                            "did you put any in conditionals?" +
                            " before " + context.getCurrEffectsIdx() + ", " +
                            "after " + newCtx.getCurrEffectsIdx());

                if (deepUpdate) {
                    // We only register the deps change if we are deep updating, otherwise
                    // if we have a soft update followed by a deep update, we would in practise
                    // skip the deep update also on the second call, as the first would consume
                    // the deps change
                    prevBody = body(true); // Force explicit, we have the lookup as the body was run

                    if (depsChanged) {
                        updateAttributes(newCtx);
                        updateEffects();
                    }
                }
                context = newCtx;
            } catch (Throwable t) {
                Throwable bodyStackTrace = newCtx != null ? newCtx.getCapturedBodyStacktrace() : null;
                if(bodyStackTrace != null)
                    t.addSuppressed(bodyStackTrace);
                throw t;
            }
        });
    }

    protected boolean isInvokingBody() {
        return currentBodyInvocationCtx != null;
    }

    protected <T> T invokeOutsideBody(Supplier<T> fn) {
        final DeclarativeComponentInternalContext prevCurrentBodyInvocationCtx = currentBodyInvocationCtx;
        currentBodyInvocationCtx = null;
        try {
            return fn.get();
        } finally {
            currentBodyInvocationCtx = prevCurrentBodyInvocationCtx;
        }
    }

    private void updateEffects() {
        int idx = 0;
        for(Effect e : effects) {
            updateEffectWithStateDependency(idx, () -> {
                e.runEffect();
                return null;
            });
            idx++;
        }
    }

    @SuppressWarnings("unchecked")
    protected void invokeBody(IdentityFreeConsumer<O_CTX> body,
                              DeclarativeComponentInternalContext newCtx,
                              Consumer<ReservedMemoProxy<?>> reserveMemo) {
        // This cast to O_CTX has to be guaranteed by the DeclarativeComponentFactory
        body.accept((O_CTX) newCtx);
    }

    protected void updateAttributes(I_CTX newCtx) {
    }

    protected @Nullable IdentityFreeRunnable getCurrentStateDependency() {
        return currentStateDependency == NO_STATE_DEPENDENCY ? null : currentStateDependency != null
                ? currentStateDependency
                : makeStateDependency(StatefulDeclarativeComponent::triggerStateUpdate, c -> new Object[] { c });
    }

    protected <RET> RET withStateDependency(@Nullable IdentityFreeRunnable stateDependency, Supplier<RET> supp) {
        IdentityFreeRunnable prevStateDependency = currentStateDependency;
        this.currentStateDependency = stateDependency;
        try {
            return supp.get();
        } finally {
            this.currentStateDependency = prevStateDependency;
        }
    }

    @SuppressWarnings("unchecked")
    public <C extends StatefulDeclarativeComponent<?, ?, ?>> IdentityFreeRunnable makeStateDependency(
            Consumer<C> runnable,
            Function<C, Object[]> deps) {
        Supplier<StatefulDeclarativeComponent<R, O_CTX, I_CTX>> componentRef = substituteComponentRef::get;
        return IdentityFreeRunnable.Impl.explicit(
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
    private <V, M extends Memoized<V>> IdentityFreeRunnable makeMemoStateDependency(
            int memoIdx,
            BiConsumer<StatefulDeclarativeComponent<R, O_CTX, I_CTX>, M> runnable,
            BiFunction<StatefulDeclarativeComponent<R, O_CTX, I_CTX>, M, Object[]> deps) {
        return this.<StatefulDeclarativeComponent<R, O_CTX, I_CTX>>makeStateDependency(
                c -> {
                    final Memoized<?> memo;
                    if(memoIdx < c.memoizedVars.size() && (memo = c.memoizedVars.get(memoIdx)) != null)
                        runnable.accept(c, (M) memo);
                },
                c -> {
                    final Memoized<?> memo;
                    if(memoIdx < c.memoizedVars.size() && (memo = c.memoizedVars.get(memoIdx)) != null)
                        return deps.apply(c, (M) memo);
                    return new Object[] { c, "memo", memoIdx };
                });
    }

    private <RET, V, M extends Memoized<V>> RET updateMemoWithStateDependency(int memoIdx, Supplier<RET> factory) {
        // Notice how it's not capturing neither this nor attr, as both  might be replaced with
        // newer versions, and we do not want to update stale stuff
        IdentityFreeRunnable stateDependency = this.<V, M>makeMemoStateDependency(
                memoIdx,
                (c, memo) -> {
                    // Mark the memo to be updated, so if for any reason its parent component is scheduled
                    // before this, and therefore it's re-run before we can get to the update scheduled below,
                    // the memo is updated right away (and not on the schedule below)
                    memo.markForUpdate();
                    // Schedule an update
                    c.scheduleOnFrameworkThread(MEMO_UPDATE_PRIORITY, () -> {
                        if(!memo.isMarkedForUpdate())
                            return;
                        // If when we get here the memo was not already updated, do it now
                        final StatefulDeclarativeComponent<R, O_CTX, I_CTX> sub = c.substituteComponentRef.get();
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
        return withStateDependency(stateDependency, factory);
    }

    private IdentityFreeRunnable makeEffectStateDependency(
            int effectIdx,
            BiConsumer<StatefulDeclarativeComponent<R, O_CTX, I_CTX>, Effect> runnable,
            BiFunction<StatefulDeclarativeComponent<R, O_CTX, I_CTX>, Effect, Object[]> deps) {
        return this.<StatefulDeclarativeComponent<R, O_CTX, I_CTX>>makeStateDependency(
                c -> {
                    final Effect effect;
                    if(effectIdx < c.effects.size() && (effect = c.effects.get(effectIdx)) != null)
                        runnable.accept(c, effect);
                },
                c -> {
                    final Effect effect;
                    if(effectIdx < c.effects.size() && (effect = c.effects.get(effectIdx)) != null)
                        return deps.apply(c, effect);
                    return new Object[] { c, "effect", effectIdx };
                });
    }

    private <RET> RET updateEffectWithStateDependency(int effectIdx, Supplier<RET> factory) {
        // Notice how it's not capturing neither this nor attr, as both  might be replaced with
        // newer versions, and we do not want to update stale stuff
        IdentityFreeRunnable stateDependency = this.makeEffectStateDependency(
                effectIdx,
                (c, effect) -> {
                    // Mark the effect to be updated, so if for any reason its parent component is scheduled
                    // before this, and therefore it's re-run before we can get to the update scheduled below,
                    // the effect is run right away (and not on the schedule below)
                    effect.markForUpdate();
                    // Schedule an update
                    c.scheduleOnFrameworkThread(EFFECT_UPDATE_PRIORITY, () -> {
                        final StatefulDeclarativeComponent<R, O_CTX, I_CTX> sub = c.substituteComponentRef.get();
                        if(sub == null)
                            return;
                        // Even if the component was substituted, effects are shallowly passed to the new one
                        // so no need to search back for it in the context
                        sub.runAsComponentUpdate(() -> sub.updateEffectWithStateDependency(effectIdx, () -> {
                            effect.runEffect();
                            return null;
                        }));
                    });
                },
                (c, effect) -> new Object[] { effect });
        return withStateDependency(stateDependency, factory);
    }

    private <RET> RET updateWithWrappedStateDependency(Predicate<StatefulDeclarativeComponent<?, ?, ?>> wrappingCond,
                                                       Supplier<RET> factory) {
        IdentityFreeRunnable wrappedStateDependency = getCurrentStateDependency();
        if(wrappedStateDependency == null)
            return factory.get();

        Collection<MethodHandles.Lookup> lookups = lookups();
        return withStateDependency(
                makeStateDependency(
                        c -> {
                            if(wrappingCond.test(c))
                                wrappedStateDependency.run();
                        },
                        c -> wrappedStateDependency.deps(lookups)),
                factory);
    }

    protected Collection<MethodHandles.Lookup> lookups() {
        return lookups;
    }

    public static Collection<MethodHandles.Lookup> currentLookups() {
        final StatefulDeclarativeComponent<?, ?, ?> currUpdatingComponent = CURR_UPDATING_COMPONENT.orElse(null);
        if(currUpdatingComponent == null)
            return Collections.emptySet();

        return currUpdatingComponent.lookups();
    }

    public static DeclarativeComponentInternalContext useInternalCtx() {
        final StatefulDeclarativeComponent<?, ?, ?> currUpdatingComponent = CURR_UPDATING_COMPONENT.orElse(null);
        if(currUpdatingComponent == null)
            throw new UnsupportedOperationException("Invalid hook invocation, can only be done inside body");

        DeclarativeComponentInternalContext ctx = currUpdatingComponent.currentBodyInvocationCtx;
        if(ctx == null)
            throw new UnsupportedOperationException("Invalid hook invocation, can only be done inside body");

        return ctx;
    }

    public static <V> V untrack(Supplier<V> value) {
        final StatefulDeclarativeComponent<?, ?, ?> currUpdatingComponent = CURR_UPDATING_COMPONENT.orElse(null);
        if(currUpdatingComponent == null)
            return value.get();

        return currUpdatingComponent.withStateDependency(NO_STATE_DEPENDENCY, value);
    }

    public static <V> void indexCollection(IdentityFreeSupplier<Collection<V>> collection0,
                                           BiConsumer<Memo.DeclareMemoFn<V>, Integer> fn) {

        final StatefulDeclarativeComponent<?, ?, ?> currUpdatingComponent = CURR_UPDATING_COMPONENT.orElse(null);
        if(currUpdatingComponent == null)
            throw new UnsupportedOperationException("Currently not in a component update");

        final IdentityFreeSupplier<Collection<V>> collection = IdentityFreeSupplier.explicit(
                currUpdatingComponent.lookups(),
                collection0);
        final AtomicReference<Integer> previousSize = new AtomicReference<>();
        currUpdatingComponent.updateWithWrappedStateDependency(c -> {
            // If the size changed from the last time we ran, we want to re-run the
            // entire block anyway (either the whole component update or an attribute
            // update)
            final int currSize = collection.get().size();
            final Integer prev = previousSize.get();
            return prev == null || prev != currSize;
        }, () -> {
            // We can access the collection here as we have set up a proper dependency
            // which will only fire updates if the size has changed
            int size = collection.get().size();
            previousSize.set(size);
            IntStream.range(0, size).forEach(i -> fn.accept(
                    // Declare memo on whatever context we are asked to
                    () -> useMemo(IdentityFreeSupplier.explicit(
                            () -> {
                                // If the collection changes this will be re-evaluated
                                Collection<V> coll = collection.get();
                                // This might be evaluated even if the size changes because of a removal,
                                // as an example in a tabbed pane we are wrapping the state dependency of a memo
                                // which will then trigger the update of the actual attributes, therefore the
                                // update order will be:
                                // 1. outer block where we iterate
                                // 2...n these components which have yet to be updated and possibly disposed
                                // n+1. tabs attribute which disposes of components (triggered by 1)
                                // n+2. updates triggered by the 2...n memos
                                // Let's just return null and hope the component gets disposed before the updates that this
                                // memo schedules are actually executed
                                if(i >= coll.size())
                                    return null;
                                if (coll instanceof List<?>)
                                    return ((List<V>) coll).get(i);
                                return coll.stream().skip(i).findFirst().orElseThrow(IndexOutOfBoundsException::new);
                            },
                            collection, i)),
                    i));
            return null;
        });
    }

    public static <V> void mapCollection(IdentityFreeSupplier<Collection<V>> collection0,
                                         BiConsumer<V, Memo.DeclareMemoFn<Integer>> fn) {

        final StatefulDeclarativeComponent<?, ?, ?> currUpdatingComponent = CURR_UPDATING_COMPONENT.orElse(null);
        if(currUpdatingComponent == null)
            throw new UnsupportedOperationException("Currently not in a component update");

        final IdentityFreeSupplier<Collection<V>> collection = IdentityFreeSupplier.explicit(
                currUpdatingComponent.lookups(),
                collection0);
        collection.get().forEach(val -> {
            // Declare memo on whatever context we are asked to
            fn.accept(val, () -> useMemo(IdentityFreeSupplier.explicit(() -> {
                // If the collection changes this will be re-evaluated
                Collection<V> coll = collection.get();
                if (coll instanceof List<?>)
                    return ((List<V>) coll).indexOf(val);
                int i = 0;
                for(V candidate : coll) {
                    if(candidate.equals(val))
                        return i;
                    i++;
                }
                // This might be evaluated even if the size changes because of a removal,
                // as an example in a tabbed pane we are wrapping the state dependency of a memo
                // which will then trigger the update of the actual attributes, therefore the
                // update order will be:
                // 1. outer block where we iterate
                // 2...n these components which have yet to be updated and possibly disposed
                // n+1. tabs attribute which disposes of components (triggered by 1)
                // n+2. updates triggered by the 2...n memos
                // Let's just return -1 and hope the component gets disposed before the updates that this
                // memo schedules are actually executed
                return -1;
            }, collection, val)));
        });
    }

    protected void disposeComponent() {
        substituteComponentRef.set(null);
    }

    @Override
    public String toString() {
        return "StatefulDeclarativeComponent{" +
                "body=" + bodyFn +
                ", memoizedVars=" + memoizedVars +
                ", effects=" + effects +
                ", context=" + context +
                ", currentBodyInvocationCtx=" + currentBodyInvocationCtx +
                '}';
    }

    static class StatefulContext implements DeclarativeComponentContext, DeclarativeComponentInternalContext {

        // This is only used internally as a placeholder, as null is already taken as a valid value
        @SuppressWarnings("StaticAssignmentOfThrowable")
        private static final Throwable STACKTRACE_SENTINEL = new Exception("StatefulContext sentinel");
        private static final String DUI_PACKAGE;
        static {
            String name = StatefulDeclarativeComponent.class.getName();
            DUI_PACKAGE = name.substring(0, name.lastIndexOf("."));
        }

        private final StatefulDeclarativeComponent<?, ?, ?> outer;
        private int currMemoizedIdx;
        private int currEffectsIdx;
        private @Nullable Throwable capturedBodyStacktrace;

        StatefulContext(StatefulDeclarativeComponent<?, ?, ?> outer) {
            this.outer = outer;
            this.currMemoizedIdx = 0;
            this.currEffectsIdx = 0;
        }

        StatefulContext(StatefulDeclarativeComponent<?, ?, ?> outer, StatefulContext other) {
            this.outer = outer;
            this.currMemoizedIdx = other.currMemoizedIdx;
            this.currEffectsIdx = other.currEffectsIdx;
            this.capturedBodyStacktrace = other.capturedBodyStacktrace;
        }

        protected int getCurrMemoizedIdx() {
            return currMemoizedIdx;
        }

        protected int getCurrEffectsIdx() {
            return currEffectsIdx;
        }

        protected @Nullable Throwable getCapturedBodyStacktrace() {
            return capturedBodyStacktrace != STACKTRACE_SENTINEL ? capturedBodyStacktrace : null;
        }

        protected void ensureInsideBody() {
            if(!outer.isInvokingBody())
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
        public void grantAccess(Supplier<MethodHandles.Lookup> supplier) {
            Memoized<MethodHandles.Lookup> memo = doUseMemo(
                    IdentityFreeSupplier.neverChange(supplier),
                    Objects::equals);
            // Access directly to avoid setting a signal dependency by calling get()
            outer.lookups.add(memo.value);
        }

        @Override
        public <V> State<V> useState(V value, BiPredicate<V, V> equalityFn) {
            ensureInsideBody();
            return this.<V>useState(() -> value, equalityFn);
        }

        @Override
        public <V> State<V> useState(Supplier<V> value, BiPredicate<V, V> equalityFn) {
            Memoized<State<V>> memo = doUseMemo(
                    IdentityFreeSupplier.neverChange(() -> new StateImpl<>(Memo.untrack(value), equalityFn)),
                    (prev, next) -> false);
            return memo.value; // Access directly to avoid setting a signal dependency by calling get()
        }

        @Override
        public <V> Memo<V> useMemo(IdentityFreeSupplier<V> value, BiPredicate<V, V> equalityFn) {
            return doUseMemo(value, equalityFn);
        }

        private <V> Memoized<V> doUseMemo(IdentityFreeSupplier<V> value, BiPredicate<V, V> equalityFn) {
            ensureInsideBody();

            // Try to catch memo issues as soon as possible from within the component
            // body so that the stacktrace is more helpful
            if (outer.context != null && getCurrMemoizedIdx() > outer.context.getCurrMemoizedIdx())
                throw new UnsupportedOperationException("Memoized variables increased in this rerender, " +
                        "did you put any state/memo in conditionals?" +
                        " before " + getCurrMemoizedIdx() + ", " +
                        "now" + outer.context.getCurrMemoizedIdx());

            final int idx = currMemoizedIdx++;
            return doUseMemo(idx, IdentityFreeSupplier.explicit(outer.lookups(), value), equalityFn);
        }

        @Override
        public <V> Ref<V> useRef(Supplier<V> fallbackValue) {
            return Hooks.useMemo(IdentityFreeSupplier.neverChange(() -> new RefImpl<>(fallbackValue))).get();
        }

        @Override
        public void useLaunchedEffect(IdentityFreeThrowingRunnable effect) {
            useDisposableEffect(IdentityFreeConsumer.explicit(scope -> {
                Future<?> future = outer.getAppConfig().launchedEffectsExecutor().submit(() -> {
                    try {
                        effect.run();
                    } catch (InterruptedException ex) {
                        // Interrupted, this got disposed
                    } catch (Throwable t) {
                        LOGGER.log(Level.SEVERE, "Effect terminated with failure", t);
                    }
                });
                scope.onDispose(() -> future.cancel(true));
            }, effect));
        }

        @Override
        public void useDisposableEffect(IdentityFreeConsumer<DisposableEffectScope> effect0) {
            ensureInsideBody();

            // Try to catch effect issues as soon as possible from within the component
            // body so that the stacktrace is more helpful
            if (outer.context != null && getCurrEffectsIdx() > outer.context.getCurrEffectsIdx())
                throw new UnsupportedOperationException("Number of effects increased in this rerender, " +
                        "did you put any in conditionals?" +
                        " before " + getCurrEffectsIdx() + ", " +
                        "now" + outer.context.getCurrEffectsIdx());

            final IdentityFreeConsumer<DisposableEffectScope> effect = IdentityFreeConsumer.explicit(
                    outer.lookups(),
                    effect0);
            final int index = currEffectsIdx++;
            if(index < outer.effects.size()) {
                final Effect effectImpl = outer.effects.get(index);
                outer.updateEffectWithStateDependency(index, () -> {
                    effectImpl.updateIfNecessary(effect);
                    return null;
                });
                return;
            }

            final Effect newEffectImpl = outer.updateEffectWithStateDependency(index, () -> new Effect(effect));
            outer.effects.add(newEffectImpl);
        }

        @Override
        public void useSideEffect(Runnable effect) {
            useDisposableEffect(IdentityFreeConsumer.alwaysChange(onDispose -> effect.run()));
        }

        protected <V> void reserveMemo(ReservedMemoProxy<V> reservedMemoProxy) {
            final int idx = currMemoizedIdx++;
            final BiPredicate<V, V> equalityFn = reservedMemoProxy.getEqualityFn();
            final Collection<MethodHandles.Lookup> lookups = outer.lookups();
            reservedMemoProxy.setReservedMemo(fn ->
                    doUseMemo(idx, IdentityFreeSupplier.explicit(lookups, fn), equalityFn));
        }

        @SuppressWarnings("unchecked")
        private <V> Memoized<V> doUseMemo(int index, IdentityFreeSupplier<V> value, BiPredicate<V, V> equalityFn) {
            if(index < outer.memoizedVars.size() && outer.memoizedVars.get(index) != null) {
                final Memoized<V> memo = (Memoized<V>) outer.memoizedVars.get(index);
                return outer.updateMemoWithStateDependency(
                        index, () -> outer.invokeOutsideBody(() -> memo.updateIfNecessary(value)));
            }

            final Memoized<V> newMemo = outer.updateMemoWithStateDependency(
                    index, () -> outer.invokeOutsideBody(() -> new Memoized<>(value, equalityFn)));

            if(LOGGER.isLoggable(Level.FINE)) {
                Object[] deps = newMemo.supplier.deps(outer.lookups());
                LOGGER.log(Level.FINE, "Created memoized value ({0}) {1} for {2}",
                        new Object[] { index, newMemo.value, Arrays.toString(deps) });
            }

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
    }

    private static class BaseMemo<V> implements Memo<V>, InternalMemo, IdentityFreeSupplier.Explicit<V> {

        private static final Object[] NO_DEPS = new Object[] {};

        private final Map<Runnable, SignalDep<V, ?>> signalDeps = new LinkedHashMap<>();

        private final BiPredicate<V, V> equalityFn;
        protected V value;

        public BaseMemo(BiPredicate<V, V> equalityFn) {
            this.equalityFn = equalityFn;
        }

        @Override
        public Object[] deps(Collection<MethodHandles.Lookup> lookups) {
            // It should be fine here to say we have no dependency,
            // as the changes will be triggered by the signals (see the method right after)
            return NO_DEPS;
        }

        @Override
        public V unsafeGet() {
            return get();
        }

        @Override
        public V get() {
            final StatefulDeclarativeComponent<?, ?, ?> currUpdatingComponent = CURR_UPDATING_COMPONENT.orElse(null);
            if(currUpdatingComponent != null) {
                Runnable currDependency = currUpdatingComponent.getCurrentStateDependency();
                if (currDependency != null)
                    signalDeps.compute(currDependency, (k, prevDep) -> prevDep == null
                            ? new SignalDep<>(k, null, null, equalityFn)
                            : prevDep.combine(null, null));
            }

            return value;
        }

        @Override
        public <R> R map(Function<V, R> mapFn, BiPredicate<R, R> equalityFn) {
            final StatefulDeclarativeComponent<?, ?, ?> currUpdatingComponent = CURR_UPDATING_COMPONENT.orElse(null);
            if(currUpdatingComponent != null) {
                Runnable currDependency = currUpdatingComponent.getCurrentStateDependency();
                if (currDependency != null) {
                    signalDeps.compute(currDependency, (k, prevDep) -> prevDep == null
                            ? new SignalDep<>(k, value, mapFn, equalityFn)
                            : prevDep.combine(value, mapFn));
                    return mapFn.apply(value);
                }
            }

            return mapFn.apply(value);
        }

        protected boolean trySet(V value) {
            if(equalityFn.test(this.value, value))
                return false;

            this.value = value;

            Set<SignalDep<V, ?>> dependencies = new LinkedHashSet<>(this.signalDeps.values());
            dependencies.removeIf(e -> e.hasNotChanged(value));
            this.signalDeps.values().removeAll(dependencies);
            dependencies.forEach(d -> d.dependency.run());
            return true;
        }

        @Override
        public int getSignalDependenciesSize() {
            return signalDeps.size();
        }

        private static class SignalDep<T, R> {
            final Runnable dependency;
            final @Nullable Function<T, R> mapFn;
            final @Nullable R value;
            final BiPredicate<R, R> equalityFn;

            public SignalDep(Runnable dependency,
                             @Nullable T val,
                             @Nullable Function<T, R> mapFn,
                             BiPredicate<R, R> equalityFn) {
                this.dependency = dependency;
                this.mapFn = mapFn;
                this.value = mapFn != null ? mapFn.apply(val) : null;
                this.equalityFn = equalityFn;
            }

            boolean hasNotChanged(T val) {
                return mapFn != null && equalityFn.test(value, mapFn.apply(val));
            }

            @SuppressWarnings("unchecked")
            <NR> SignalDep<T, ?> combine(@Nullable T val, @Nullable Function<T, NR> mapFn) {
                // If either of the two is an unconditional signal, just return something that runs all the time anyway
                if (this.mapFn == null)
                    return this;
                if (mapFn == null)
                    return new SignalDep<>(dependency, null, null, Objects::deepEquals);

                // Combine the mapping functions
                List<Function<T, ?>> mappers;
                if (this.mapFn instanceof CombinedMapper && mapFn instanceof CombinedMapper) {
                    mappers = new ArrayList<>(((CombinedMapper<T>) this.mapFn).mappers);
                    mappers.addAll(((CombinedMapper<T>) mapFn).mappers);
                } else if(this.mapFn instanceof CombinedMapper) {
                    mappers = new ArrayList<>(((CombinedMapper<T>) this.mapFn).mappers);
                    mappers.add(mapFn);
                } else if(mapFn instanceof CombinedMapper) {
                    mappers = new ArrayList<>(((CombinedMapper<T>) mapFn).mappers);
                    mappers.add(this.mapFn);
                } else {
                    mappers = new ArrayList<>();
                    mappers.add(this.mapFn);
                    mappers.add(mapFn);
                }

                CombinedMapper<T> mapper = new CombinedMapper<>(Collections.unmodifiableList(mappers));
                return new SignalDep<>(dependency, val, mapper, Objects::deepEquals);
            }

            static class CombinedMapper<T> implements Function<T, Object> {

                final List<Function<T, ?>> mappers;

                public CombinedMapper(List<Function<T, ?>> mappers) {
                    this.mappers = mappers;
                }

                @Override
                public Object apply(T t) {
                    return mappers.stream().map(m -> m.apply(t)).collect(Collectors.toList());
                }
            }
        }
    }

    private static class Memoized<V> extends BaseMemo<V> {

        private IdentityFreeSupplier<V> supplier;
        private boolean markedForUpdate;

        public Memoized(IdentityFreeSupplier<V> supplier, BiPredicate<V, V> equalityFn) {
            super(equalityFn);
            this.value = supplier.get();
            this.supplier = supplier;
        }

        public void markForUpdate() {
            markedForUpdate = true;
        }

        public boolean isMarkedForUpdate() {
            return markedForUpdate;
        }

        public void update() {
            boolean wasSet = trySet(supplier.get());

            if(wasSet && LOGGER.isLoggable(Level.FINE))
                LOGGER.log(Level.FINE, "Updated memoized value {0} (deps: {1})",
                        new Object[] { value, Arrays.toString(supplier.deps()) });

            markedForUpdate = false;
        }

        public Memoized<V> updateIfNecessary(IdentityFreeSupplier<V> newValue) {
            if(markedForUpdate || !this.supplier.equals(newValue)) {
                this.supplier = newValue;
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
            trySet(value);
        }

        @Override
        public S update(Function<S, S> updater) {
            set(updater.apply(get()));
            return get();
        }
    }

    private static class Effect {

        private IdentityFreeConsumer<DisposableEffectScope> effect;
        private boolean shouldRun;
        private @Nullable Runnable onDispose;

        public Effect(IdentityFreeConsumer<DisposableEffectScope> effect) {
            this.effect = effect;
            this.shouldRun = true;
        }

        public void markForUpdate() {
            this.shouldRun = true;
        }

        public void updateIfNecessary(IdentityFreeConsumer<DisposableEffectScope> effect) {
            // Avoid deepEquals if we should already run anyway
            if(shouldRun)
                return;
            if (Objects.equals(this.effect, effect))
                return;

            this.effect = effect;
            markForUpdate();
        }

        public void runEffect() {
            if(!shouldRun)
                return;

            shouldRun = false;
            disposeEffect();
            effect.accept(onDispose -> this.onDispose = onDispose);
        }

        public void disposeEffect() {
            if(onDispose != null)
                onDispose.run();
            onDispose = null;
        }
    }

    static class RefImpl<T> implements Ref<T> {

        private final Supplier<T> fallback;
        private T ref;
        private boolean wasSet;

        RefImpl(Supplier<T> fallback) {
            this.fallback = fallback;
        }

        @Override
        public T curr() {
            if(!wasSet)
                curr(fallback.get());
            return ref;
        }

        @Override
        public void curr(T ref) {
            this.ref = ref;
            this.wasSet = true;
        }
    }
}
