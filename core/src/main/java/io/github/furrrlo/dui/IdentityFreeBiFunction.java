package io.github.furrrlo.dui;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.function.BiFunction;

public interface IdentityFreeBiFunction<T, U, R> extends BiFunction<T, U, R>, IdentityFree, Serializable {

    @Override
    default Object[] deps(Collection<MethodHandles.Lookup> lookups) {
        return IdentityFrees.computeDependencies(lookups, this);
    }

    interface Explicit<T, U, R> extends IdentityFreeBiFunction<T, U, R>, IdentityFree.Explicit {

        @Override
        Object[] deps(Collection<MethodHandles.Lookup> lookups);
    }

    static <T, U, R> Explicit<T, U, R> explicit(Collection<MethodHandles.Lookup> lookups, IdentityFreeBiFunction<T, U, R> fn) {
        return fn instanceof Explicit
                ? (Explicit<T, U, R>) fn
                : new Impl.ExplicitArray<>(lookups, fn, fn.deps(lookups));
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
            private final IdentityFreeDeps deps;

            public ExplicitArray(BiFunction<T, U, R> fn, Object[] deps) {
                this.fn = fn;
                this.deps = IdentityFreeDeps.of(deps);
            }

            public ExplicitArray(Collection<MethodHandles.Lookup> lookups, BiFunction<T, U, R> fn, Object[] deps) {
                this.fn = fn;
                this.deps = IdentityFreeDeps.immediatelyExplicit(lookups, deps);
            }

            @Override
            public R apply(T t, U u) {
                return fn.apply(t, u);
            }

            @Override
            public Object[] deps(Collection<MethodHandles.Lookup> lookups) {
                return deps.get(lookups);
            }

            @Override
            public Class<?> getImplClass() {
                return fn.getClass();
            }

            @Override
            @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
            public boolean equals(Object o) {
                return IdentityFrees.equals(this, o);
            }

            @Override
            public int hashCode() {
                return IdentityFrees.hashCode(this);
            }

            @Override
            public String toString() {
                return "ExplicitArray{" +
                        "fn=" + fn +
                        ", deps=" + deps +
                        '}';
            }
        }
    }
}
