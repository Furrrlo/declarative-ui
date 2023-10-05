package io.github.furrrlo.dui;

import java.util.function.Function;

public interface State<T> extends Memo<T> {

    void set(T value);

    T update(Function<T, T> updater);
}
