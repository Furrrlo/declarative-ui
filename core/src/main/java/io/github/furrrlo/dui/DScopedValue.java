package io.github.furrrlo.dui;

import org.jetbrains.annotations.Contract;

interface DScopedValue<T> {

    static <T> DScopedValue<T> create() {
        return Inner.IS_SCOPED_VALUE_AVAILABLE ? Inner.newActualScopedValue() : new DThreadLocalScopedValue<>();
    }

    void where(T value, Runnable runnable);

    boolean isBound();

    T get();

    @Contract("!null -> !null; _ -> _")
    T orElse(T other);

    class Inner {

        private static final boolean IS_SCOPED_VALUE_AVAILABLE;
        static {
            boolean isScopedValueAvailable;
            try {
                new DActualScopedValue<>();
                isScopedValueAvailable = true;
            } catch (Throwable t) {
                isScopedValueAvailable = false;
            }

            IS_SCOPED_VALUE_AVAILABLE = isScopedValueAvailable;
        }

        private Inner() {
        }

        private static <T> DScopedValue<T> newActualScopedValue() {
            return new DActualScopedValue<>();
        }
    }
}
