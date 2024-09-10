package io.github.furrrlo.dui;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

class DActualScopedValue<T> implements DScopedValue<T> {

    private static final MethodHandle SCOPED_VALUE_RUN_WHERE;
    private static final MethodHandle SCOPED_VALUE_NEW_INSTANCE;
    private static final MethodHandle SCOPED_VALUE_GET;
    private static final MethodHandle SCOPED_VALUE_OR_ELSE;
    private static final MethodHandle SCOPED_VALUE_IS_BOUND;
    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.publicLookup();
            Class<?> claz = Class.forName("java.lang.ScopedValue");
            SCOPED_VALUE_NEW_INSTANCE = lookup.unreflect(claz.getMethod("newInstance"));
            SCOPED_VALUE_RUN_WHERE = lookup.unreflect(claz.getMethod("runWhere", claz, Object.class, Runnable.class));
            SCOPED_VALUE_GET = lookup.unreflect(claz.getMethod("get"));
            SCOPED_VALUE_OR_ELSE = lookup.unreflect(claz.getMethod("orElse", Object.class));
            SCOPED_VALUE_IS_BOUND = lookup.unreflect(claz.getMethod("isBound"));
        } catch (IllegalAccessException | ClassNotFoundException | NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private final Object actual;

    public DActualScopedValue() {
        try {
            this.actual = SCOPED_VALUE_NEW_INSTANCE.invoke();
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable ex) {
            throw new AssertionError("Unexpected checked exception", ex);
        }
    }

    @Override
    public void where(T value, Runnable runnable) {
        try {
            SCOPED_VALUE_RUN_WHERE.invoke(actual, value, runnable);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable ex) {
            throw new AssertionError("Unexpected checked exception", ex);
        }
    }

    @Override
    public boolean isBound() {
        try {
            return (boolean) SCOPED_VALUE_IS_BOUND.invoke(actual);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable ex) {
            throw new AssertionError("Unexpected checked exception", ex);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get() {
        try {
            return (T) SCOPED_VALUE_GET.invoke(actual);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable ex) {
            throw new AssertionError("Unexpected checked exception", ex);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T orElse(T other) {
        try {
            return (T) SCOPED_VALUE_OR_ELSE.invoke(actual, other);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable ex) {
            throw new AssertionError("Unexpected checked exception", ex);
        }
    }
}
