package io.github.furrrlo.dui;

import java.io.Serializable;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface IdentifiableBiFunction<T, U, R> extends BiFunction<T, U, R>, Identifiable, Serializable {

    @Override
    default Object[] deps() {
        return Identifiables.computeDependencies(this);
    }

    interface Explicit<T, U, R> extends IdentifiableBiFunction<T, U, R>, Identifiable.Explicit {

        @Override
        Object[] deps();
    }

    static <T, U, R> Explicit<T, U, R> explicit(IdentifiableBiFunction<T, U, R> fn) {
        return fn instanceof Explicit
                ? (Explicit<T, U, R>) fn
                : new Impl.ExplicitArray<>(fn, fn.deps());
    }

    static <T, U, R> Explicit<T, U, R> explicit(BiFunction<T, U, R> fn, Object... deps) {
        return new Impl.ExplicitArray<>(fn, deps);
    }

    static <T, U, R> Explicit<T, U, R> explicit(BiFunction<T, U, R> fn, Supplier<Object[]> deps) {
        return new Impl.ExplicitSupplier<>(fn, deps);
    }

    static <T, U, R> Explicit<T, U, R> alwaysChange(BiFunction<T, U, R> fn) {
        return new Impl.ExplicitArray<>(fn, new Object[] { /* Force to always change */ new Object() });
    }

    class Impl {

        private static class ExplicitArray<T, U, R> implements Explicit<T, U, R> {

            private final transient BiFunction<T, U, R> fn;
            private final Object[] deps;

            public ExplicitArray(BiFunction<T, U, R> fn, Object[] deps) {
                this.fn = fn;
                this.deps = deps;
            }

            @Override
            public R apply(T t, U u) {
                return fn.apply(t, u);
            }

            @Override
            public Object[] deps() {
                return deps;
            }

            @Override
            public Class<?> getImplClass() {
                return fn.getClass();
            }

            @Override
            @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
            public boolean equals(Object o) {
                return Identifiables.equals(this, o);
            }

            @Override
            public int hashCode() {
                return Identifiables.hashCode(this);
            }
        }

        private static class ExplicitSupplier<T, U, R> implements Explicit<T, U, R> {

            private final transient BiFunction<T, U, R> fn;
            private final transient Supplier<Object[]> deps; // TODO: serialize the result

            public ExplicitSupplier(BiFunction<T, U, R> fn, Supplier<Object[]> deps) {
                this.fn = fn;
                this.deps = deps;
            }

            @Override
            public R apply(T t, U u) {
                return fn.apply(t, u);
            }

            @Override
            public Object[] deps() {
                return deps.get();
            }

            @Override
            public Class<?> getImplClass() {
                return fn.getClass();
            }

            @Override
            @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
            public boolean equals(Object o) {
                return Identifiables.equals(this, o);
            }

            @Override
            public int hashCode() {
                return Identifiables.hashCode(this);
            }
        }
    }
}
