package io.github.furrrlo.dui;

class DThreadLocalScopedValue<T> implements DScopedValue<T> {

    private final ThreadLocal<T> threadLocal = ThreadLocal.withInitial(() -> null);

    @Override
    public void where(T value, Runnable runnable) {
        final T prev = threadLocal.get();
        threadLocal.set(value);
        try {
            runnable.run();
        } finally {
            if(prev == null)
                threadLocal.remove();
            else
                threadLocal.set(prev);
        }
    }

    @Override
    public boolean isBound() {
        T res = threadLocal.get();
        if(res != null)
            return true;

        threadLocal.remove();
        return false;
    }

    @Override
    public T get() {
        T res = threadLocal.get();
        if(res != null)
            return res;

        threadLocal.remove();
        throw new IllegalStateException();
    }

    @Override
    public T orElse(T other) {
        T res = threadLocal.get();
        if(res != null)
            return res;

        threadLocal.remove();
        return other;
    }
}
