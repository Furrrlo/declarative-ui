package io.github.furrrlo.dui;

import java.util.function.Supplier;

public interface Memo<T> extends Supplier<T> {

    T get();
}
