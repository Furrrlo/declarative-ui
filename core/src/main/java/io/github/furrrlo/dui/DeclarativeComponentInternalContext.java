package io.github.furrrlo.dui;

import io.github.furrrlo.dui.Hooks.DisposableEffectScope;

import java.lang.invoke.MethodHandles;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

public interface DeclarativeComponentInternalContext extends DeclarativeComponentContext {

    void grantAccess(Supplier<MethodHandles.Lookup> lookup);

    <V> State<V> useState(V value, BiPredicate<V, V> equalityFn);

    <V> State<V> useState(Supplier<V> value, BiPredicate<V, V> equalityFn);

    <V> Memo<V> useMemo(IdentityFreeSupplier<V> value, BiPredicate<V, V> equalityFn);

    <V> Ref<V> useRef(Supplier<V> initialValue);

    void useLaunchedEffect(IdentityFreeThrowingRunnable effect);

    void useDisposableEffect(IdentityFreeConsumer<DisposableEffectScope> effect);

    void useSideEffect(Runnable effect);
}
