package io.github.furrrlo.dui;

import io.leangen.geantyref.TypeToken;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.*;

public abstract class DeclarativeComponentContextDecorator<T> implements DeclarativeRefComponentContext<T> {

    private final @Nullable TypeToken<T> type;
    private final List<Type> typeArguments;
    private final Supplier<@Nullable T> factory;
    private final BooleanSupplier canUpdateInCurrentThread;
    private final UpdateScheduler updateScheduler;

    private @Nullable DeclarativeRefComponentContext<T> toDecorate;
    private final List<ReservedMemoProxy<?>> reservedMemos = new ArrayList<>();

    protected DeclarativeComponentContextDecorator(@Nullable Class<T> type,
                                                   Supplier<@Nullable T> factory,
                                                   BooleanSupplier canUpdateInCurrentThread,
                                                   FrameworkScheduler frameworkScheduler) {
        this(type != null ? TypeToken.get(type) : null, factory, canUpdateInCurrentThread, frameworkScheduler);
    }

    protected DeclarativeComponentContextDecorator(@Nullable TypeToken<T> type,
                                                   Supplier<@Nullable T> factory,
                                                   BooleanSupplier canUpdateInCurrentThread,
                                                   FrameworkScheduler frameworkScheduler) {
        this.type = type;
        this.typeArguments = resolveTypeArguments(type);
        this.factory = factory;
        this.canUpdateInCurrentThread = canUpdateInCurrentThread;
        this.updateScheduler = frameworkScheduler.updateScheduler;
    }

    private static List<Type> resolveTypeArguments(@Nullable TypeToken<?> type) {
        if(type == null)
            return Collections.emptyList();

        Type reflectedType = type.getType();
        if(reflectedType instanceof ParameterizedType) {
            ParameterizedType parametizedType = (ParameterizedType) reflectedType;
            return Arrays.asList(parametizedType.getActualTypeArguments());
        }

        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public <E> TypeToken<E> getLiteralTypeArgumentAt(int index) {
        TypeToken<E> type = (TypeToken<E>) TypeToken.get(typeArguments.get(index));
        Type actualType = type.getType();
        if(actualType instanceof Class ||
                actualType instanceof ParameterizedType ||
                actualType instanceof GenericArrayType)
            return type;

        throw new UnsupportedOperationException("Type argument at index " + index + " is not a concrete type: " + type);
    }

    void setToDecorate(@Nullable DeclarativeRefComponentContext<T> toDecorate, Consumer<ReservedMemoProxy<?>> reserveMemo) {
        this.toDecorate = toDecorate;
        this.reservedMemos.forEach(reserveMemo);
    }

    void endDecoration() {
        this.toDecorate = null;
        this.reservedMemos.forEach(ReservedMemoProxy::endDecoration);
    }

    private DeclarativeRefComponentContext<T> toDecorate() {
        return Objects.requireNonNull(
                toDecorate,
                "Missing object to decorate");
    }

    // Internal decoration api

    protected <V> ReservedMemo<V> reserveMemo(IdentifiableSupplier<V> fallbackValue) {
        return reserveMemo(fallbackValue, Objects::deepEquals);
    }

    protected <V> ReservedMemo<V> reserveMemo(IdentifiableSupplier<V> fallbackValue, BiPredicate<V, V> equalityFn) {
        if(toDecorate != null)
            throw new UnsupportedOperationException("Too late to reserve memos");

        ReservedMemoProxy<V> proxy = new ReservedMemoProxy<>(fallbackValue, equalityFn);
        reservedMemos.add(proxy);
        return proxy;
    }

    protected interface ReservedMemo<T> extends Function<IdentifiableSupplier<T>, Memo<T>> {
        @Override
        Memo<T> apply(IdentifiableSupplier<T> value);
    }

    static class ReservedMemoProxy<T> implements ReservedMemo<T> {

        private final IdentifiableSupplier<T> fallbackValue;
        private final BiPredicate<T, T> equalityFn;

        private @Nullable ReservedMemo<T> actual;
        private boolean wasRun;

        public ReservedMemoProxy(IdentifiableSupplier<T> fallbackValue, BiPredicate<T, T> equalityFn) {
            this.fallbackValue = fallbackValue;
            this.equalityFn = equalityFn;
        }

        public BiPredicate<T, T> getEqualityFn() {
            return equalityFn;
        }

        void setReservedMemo(ReservedMemo<T> actual) {
            this.actual = actual;
        }

        @Override
        public Memo<T> apply(IdentifiableSupplier<T> value) {
            if(wasRun)
                throw new UnsupportedOperationException("Memo was already reserved");
            wasRun = true;
            return Objects.requireNonNull(actual, "Memo wasn't reserved yet").apply(value);
        }

        protected void endDecoration() {
            if(!wasRun)
                apply(fallbackValue);
            actual = null;
            wasRun = false;
        }
    }

    // Getters

    public @Nullable TypeToken<T> getType() {
        return type;
    }

    public Supplier<@Nullable T> getFactory() {
        return factory;
    }

    UpdateScheduler getUpdateScheduler() {
        return updateScheduler;
    }

    public BooleanSupplier getCanUpdateInCurrentThread() {
        return canUpdateInCurrentThread;
    }

    public DeclarativeComponentFactory fn() {
        return DeclarativeComponentFactory.INSTANCE;
    }

    // Delegate

    @Override
    public void ref(Ref<? super T> ref) {
        toDecorate().ref(ref);
    }

    @Override
    public void ref(Consumer<? super T> ref) {
        toDecorate().ref(ref);
    }

    @Override
    public <V> State<V> useState(V value) {
        return toDecorate().useState(value);
    }

    @Override
    public <V> State<V> useState(V value, BiPredicate<V, V> equalityFn) {
        return toDecorate().useState(value, equalityFn);
    }

    @Override
    public <V> State<V> useState(Supplier<V> value) {
        return toDecorate().useState(value);
    }

    @Override
    public <V> State<V> useState(Supplier<V> value, BiPredicate<V, V> equalityFn) {
        return toDecorate().useState(value, equalityFn);
    }

    @Override
    public <V> Memo<V> useMemo(IdentifiableSupplier<V> value) {
        return toDecorate().useMemo(value);
    }

    @Override
    public <V> Memo<V> useMemo(IdentifiableSupplier<V> value, BiPredicate<V, V> equalityFn) {
        return toDecorate().useMemo(value, equalityFn);
    }

    @Override
    public <V extends Serializable> V useCallback(V fun) {
        return toDecorate().useCallback(fun);
    }

    @Override
    public <V> V useCallbackExplicit(V fun, Object dependency) {
        return toDecorate().useCallbackExplicit(fun, dependency);
    }

    @Override
    public <V> V useCallbackExplicit(V fun, List<Object> dependencies) {
        return toDecorate().useCallbackExplicit(fun, dependencies);
    }

    @Override
    public <V> Ref<V> useRef(Supplier<V> fallbackValue) {
        return toDecorate().useRef(fallbackValue);
    }

    @Override
    public void useLaunchedEffect(IdentifiableThrowingRunnable effect) {
        toDecorate().useLaunchedEffect(effect);
    }

    @Override
    public void useDisposableEffect(IdentifiableConsumer<DisposableEffectScope> effect) {
        toDecorate().useDisposableEffect(effect);
    }

    @Override
    public void useSideEffect(Runnable effect) {
        toDecorate().useSideEffect(effect);
    }

    @Override
    public <V> DeclarativeRefComponentContext<T> inner(Function<T, V> getter, DeclarativeComponent<V> component) {
        return toDecorate().inner(getter, component);
    }

    @Override
    public <V> DeclarativeRefComponentContext<T> attribute(String key, BiConsumer<T, V> setter, Supplier<? extends V> value) {
        return toDecorate().attribute(key, setter, value);
    }

    @Override
    public <V> DeclarativeRefComponentContext<T> attribute(String key,
                                                           BiConsumer<T, V> setter,
                                                           Supplier<? extends V> value,
                                                           AttributeEqualityFn<T, V> equalityFn) {
        return toDecorate().attribute(key, setter, value, equalityFn);
    }

    @Override
    public <V, S extends DeclarativeComponentWithIdSupplier<? extends V>> DeclarativeRefComponentContext<T> listAttribute(
            String key,
            TypeToken<V> type,
            ListReplacer<T, V, S> replacer,
            Supplier<List<V>> fn
    ) {
        return toDecorate().listAttribute(key, type, replacer, fn);
    }

    @Override
    public <V, S extends DeclarativeComponentWithIdSupplier<? extends V>> DeclarativeRefComponentContext<T> listAttribute(
            String key,
            TypeToken<V> type,
            ListRemover<T> remover,
            Supplier<List<V>> fn,
            ListAdder<T, V, S> adder
    ) {
        return toDecorate().listAttribute(key, type, remover, fn, adder);
    }

    @Override
    public <C> DeclarativeRefComponentContext<T> fnAttribute(String key,
                                                             BiConsumer<T, C> setter,
                                                             @Nullable DeclarativeComponentSupplier<? extends C> fn) {
        return toDecorate().fnAttribute(key, setter, fn);
    }

    @Override
    public <C, S extends DeclarativeComponentWithIdSupplier<? extends C>> DeclarativeRefComponentContext<T> listFnAttribute(
            String key,
            ListSetter<T, C, S> setter,
            Supplier<List<S>> fn
    ) {
        return toDecorate().listFnAttribute(key, setter, fn);
    }

    @Override
    public <C, S extends DeclarativeComponentWithIdSupplier<? extends C>> DeclarativeRefComponentContext<T> listFnAttribute(
            String key,
            ListReplacer<T, C, S> replacer,
            Supplier<List<S>> fn
    ) {
        return toDecorate().listFnAttribute(key, replacer, fn);
    }

    @Override
    public <C, S extends DeclarativeComponentWithIdSupplier<? extends C>> DeclarativeRefComponentContext<T> listFnAttribute(
            String key,
            ListAdder<T, C, S> adder,
            ListRemover<T> remover,
            Supplier<List<S>> fn
    ) {
        return toDecorate().listFnAttribute(key, adder, remover, fn);
    }
}
