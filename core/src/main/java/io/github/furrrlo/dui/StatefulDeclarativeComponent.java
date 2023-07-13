package io.github.furrrlo.dui;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract class StatefulDeclarativeComponent<
        T,
        R,
        O_CTX extends DeclarativeComponentContext<T>,
        I_CTX extends StatefulDeclarativeComponent.StatefulContext<T>> implements DeclarativeComponent<R> {

    private static final Logger LOGGER = Logger.getLogger(StatefulDeclarativeComponent.class.getName());

    protected final @Nullable Body<T, O_CTX> body;

    protected List<Memoized<?>> memoizedVars = new ArrayList<>();
    protected I_CTX context;
    protected boolean isInvokingBody;

    public StatefulDeclarativeComponent(@Nullable Body<T, O_CTX> body) {
        this.body = body;
    }

    @SuppressWarnings("unchecked")
    protected void copy(StatefulDeclarativeComponent<?, ?, ?, ?> other0) {
        ensureSame("type", other0, StatefulDeclarativeComponent::getClass);

        final StatefulDeclarativeComponent<T, R, O_CTX, I_CTX> other = (StatefulDeclarativeComponent<T, R, O_CTX, I_CTX>) other0;
        memoizedVars = other.memoizedVars;
        memoizedVars.forEach(memo -> {
            if (memo.value instanceof StateImpl)
                ((StateImpl<?>) memo.value).component = this;
        });
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

    public abstract void runOrScheduleOnFrameworkThread(Runnable runnable);

    protected void updateComponent() {
        updateComponent(true);
    }

    @SuppressWarnings("unchecked")
    protected void updateComponent(boolean deepUpdate) {
        final I_CTX newCtx = newContext();
        if(body != null) {
            isInvokingBody = true;
            // This cast to C has to be guaranteed by the DeclarativeComponentFactory
            invokeBody(body, (O_CTX) newCtx);
            isInvokingBody = false;
        }

        if(context != null && newCtx.getCurrMemoizedIdx() != context.getCurrMemoizedIdx())
            throw new UnsupportedOperationException("Memoized variables differ across re-renders, " +
                    "did you put any state/memo in conditionals?" +
                    " before " + newCtx.getCurrMemoizedIdx() + ", " +
                    "after" + newCtx.getCurrMemoizedIdx());

        if(deepUpdate)
            updateAttributes(newCtx);
        context = newCtx;
    }

    protected void invokeBody(Body<T, O_CTX> body, O_CTX newCtx) {
        body.component(newCtx);
    }

    protected void updateAttributes(I_CTX newCtx) {
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
            return useMemo(() -> new StateImpl<>(outer, value.get()));
        }

        @Override
        @SuppressWarnings("unchecked")
        public <V> V useMemo(Supplier<V> value, List<Object> dependencies) {
            ensureInsideBody();

            if(currMemoizedIdx < outer.memoizedVars.size())
                return ((Memoized<V>) outer.memoizedVars.get(currMemoizedIdx++)).updateAndGet(value, dependencies);

            final Memoized<V> newMemo = new Memoized<>(value.get(), dependencies);
            if(LOGGER.isLoggable(Level.FINE))
                LOGGER.log(Level.FINE, "Created memoized value ({0}) {1} for {2}",
                        new Object[] { currMemoizedIdx, newMemo.value, newMemo.dependencies });
            outer.memoizedVars.add(newMemo);
            currMemoizedIdx++;
            return newMemo.updateAndGet(value, dependencies);
        }

        @Override
        public <V> V useCallback(V fun, List<Object> dependencies) {
            return useMemo(() -> fun, dependencies);
        }

        @Override
        public <V> DeclarativeComponentContext<T> inner(Function<T, V> getter, DeclarativeComponent<V> component) {
            ensureInsideBody();
            return this;
        }

        @Override
        public <V> DeclarativeComponentContext<T> attribute(String key, BiConsumer<T, V> setter, V value) {
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

    private static class StateImpl<S> extends BaseState<S> {

        private StatefulDeclarativeComponent<?, ?, ?, ?> component;

        public StateImpl(StatefulDeclarativeComponent<?, ?, ?, ?> component, S value) {
            super(value);
            this.component = component;
        }

        @Override
        public void set(S value) {
            super.set(value);
            component.triggerComponentUpdate();
        }
    }

    private static class Memoized<V> {

        private V value;
        private List<Object> dependencies;

        public Memoized(V value, List<Object> dependencies) {
            this.value = value;
            this.dependencies = dependencies;
        }

        public V updateAndGet(Supplier<V> newValue, List<Object> dependencies) {
            if(!this.dependencies.equals(dependencies)) {
                value = newValue.get();
                if(LOGGER.isLoggable(Level.FINE))
                    LOGGER.log(Level.FINE, "Updated memoized value {0} (deps: {1} -> {2})",
                            new Object[] { value, this.dependencies, dependencies });
                this.dependencies = dependencies;
            }
            return value;
        }
    }
}
