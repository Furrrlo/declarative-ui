package io.github.furrrlo.dui;

import java.util.function.Function;

public interface State<T> {

    T get();

    void set(T value);

    T update(Function<T, T> updater);
}
