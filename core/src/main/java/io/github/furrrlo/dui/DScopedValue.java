package io.github.furrrlo.dui;

import org.jetbrains.annotations.Contract;

interface DScopedValue<T> {

    static <T> DScopedValue<T> create() {
        // TODO: on java versions >= 20, use a wrapper around the actual ScopedValue
        return new DThreadLocalScopedValue<>();
    }

    void where(T value, Runnable runnable);

    boolean isBound();

    T get();

    @Contract("!null -> !null; _ -> _")
    T orElse(T other);
}
