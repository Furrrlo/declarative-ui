package io.github.furrrlo.dui;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Memo<T> extends Supplier<T> {

    T get();

    static <V> void indexCollection(
            IdentifiableSupplier<Collection<V>> collection,
            BiConsumer<Function<DeclarativeComponentContext<?>, Memo<V>>, Integer> fn) {

        StatefulDeclarativeComponent.indexCollection(collection, fn);
    }
}
