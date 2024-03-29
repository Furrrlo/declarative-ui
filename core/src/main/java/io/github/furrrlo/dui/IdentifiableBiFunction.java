package io.github.furrrlo.dui;

import java.io.Serializable;
import java.util.function.BiFunction;

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

    static <T, U, R> Explicit<T, U, R> alwaysChange(BiFunction<T, U, R> fn) {
        return new Impl.ExplicitArray<>(fn, new Object[] { /* Force to always change */ new Object() });
    }

    static <T, U, R> Explicit<T, U, R> neverChange(BiFunction<T, U, R> fn) {
        return new Impl.ExplicitArray<>(fn, new Object[] {});
    }

    class Impl {

        private static class ExplicitArray<T, U, R> implements Explicit<T, U, R> {

            private final transient BiFunction<T, U, R> fn;
            private final Object[] deps;

            public ExplicitArray(BiFunction<T, U, R> fn, Object[] deps) {
                this.fn = fn;
                this.deps = Identifiables.makeDependenciesExplicit(deps);
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
    }
}
