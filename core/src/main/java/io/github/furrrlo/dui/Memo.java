package io.github.furrrlo.dui;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public interface Memo<T> extends IdentifiableSupplier<T> {

    @Override
    T get();

    static <V> V untrack(Supplier<V> value) {
        return StatefulDeclarativeComponent.untrack(value);
    }

    static <V> void indexCollection(IdentifiableSupplier<Collection<V>> collection,
                                    BiConsumer<DeclareMemoFn<V>, Integer> fn) {
        StatefulDeclarativeComponent.indexCollection(collection, fn);
    }

    static <V> void mapCollection(IdentifiableSupplier<Collection<V>> collection,
                                  BiConsumer<V, DeclareMemoFn<Integer>> fn) {
        StatefulDeclarativeComponent.mapCollection(collection, fn);
    }

    interface DeclareMemoFn<V> extends Supplier<Memo<V>> {
        @Override
        Memo<V> get();
    }
}
