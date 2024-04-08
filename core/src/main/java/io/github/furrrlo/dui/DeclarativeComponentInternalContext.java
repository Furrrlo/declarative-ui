package io.github.furrrlo.dui;

import io.github.furrrlo.dui.Hooks.DisposableEffectScope;

import java.util.function.BiPredicate;
import java.util.function.Supplier;

public interface DeclarativeComponentInternalContext extends DeclarativeComponentContext {

    <V> State<V> useState(V value, BiPredicate<V, V> equalityFn);

    <V> State<V> useState(Supplier<V> value, BiPredicate<V, V> equalityFn);

    <V> Memo<V> useMemo(IdentifiableSupplier<V> value, BiPredicate<V, V> equalityFn);

    <V> Ref<V> useRef(Supplier<V> initialValue);

    void useLaunchedEffect(IdentifiableThrowingRunnable effect);

    void useDisposableEffect(IdentifiableConsumer<DisposableEffectScope> effect);

    void useSideEffect(Runnable effect);
}
