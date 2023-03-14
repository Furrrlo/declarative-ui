package io.github.furrrlo.dui;

import java.util.function.Function;

class BaseState<T> implements State<T> {

    protected T value;

    public BaseState(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public void set(T value) {
        this.value = value;
    }

    @Override
    public T update(Function<T, T> updater) {
        set(updater.apply(get()));
        return get();
    }

    @Override
    public String toString() {
        return "BaseState{" +
                "value=" + value +
                '}';
    }
}
