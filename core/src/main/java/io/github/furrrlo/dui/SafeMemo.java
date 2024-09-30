package io.github.furrrlo.dui;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;


public interface SafeMemo<T> {

    T unsafeGet();

    default <R> R map(Function<T, R> mapFn) {
        return map(mapFn, Objects::deepEquals);
    }

    default <P, R extends Consumer<P>> void accept(Function<T, R> mapFn, P param) {
        map(mapFn).accept(param);
    }

    default <P, R, F extends Function<P, R>> R apply(Function<T, F> mapFn, P param) {
        return map(mapFn).apply(param);
    }

    <R> R map(Function<T, R> mapFn, BiPredicate<R, R> equalityFn);
}
