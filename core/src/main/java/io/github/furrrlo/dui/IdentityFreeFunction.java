package io.github.furrrlo.dui;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.function.Function;

public interface IdentityFreeFunction<T, R> extends Function<T, R>, IdentityFree, Serializable {

    @Override
    default Object[] deps(Collection<MethodHandles.Lookup> lookup) {
        return IdentityFrees.computeDependencies(lookup, this);
    }

    interface Explicit<T, R> extends IdentityFreeFunction<T, R>, IdentityFree.Explicit {

        @Override
        Object[] deps(Collection<MethodHandles.Lookup> lookups);
    }

    static <T, R> Explicit<T, R> explicit(Collection<MethodHandles.Lookup> lookups, IdentityFreeFunction<T, R> fn) {
        return fn instanceof Explicit
                ? (Explicit<T, R>) fn
                : new Impl.ExplicitArray<>(lookups, fn, fn.deps(lookups));
    }

    static <T, R> Explicit<T, R> explicit(Function<T, R> fn, Object... deps) {
        return new Impl.ExplicitArray<>(fn, deps);
    }

    static <T, R> Explicit<T, R> neverChange(Function<T, R> fn) {
        return new Impl.ExplicitArray<>(fn, new Object[] {});
    }

    static <T, R> Explicit<T, R> alwaysChange(Function<T, R> fn) {
        return new Impl.ExplicitArray<>(fn, new Object[] { /* Force to always change */ new Object() });
    }

    class Impl {

        private static class ExplicitArray<T, R> implements Explicit<T, R> {

            private final transient Function<T, R> fn;
            private final IdentityFreeDeps deps;

            public ExplicitArray(Function<T, R> fn, Object[] deps) {
                this.fn = fn;
                this.deps = IdentityFreeDeps.of(deps);
            }

            public ExplicitArray(Collection<MethodHandles.Lookup> lookups, Function<T, R> fn, Object[] deps) {
                this.fn = fn;
                this.deps = IdentityFreeDeps.immediatelyExplicit(lookups, deps);
            }

            @Override
            public R apply(T t) {
                return fn.apply(t);
            }

            @Override
            public Object[] deps(Collection<MethodHandles.Lookup> lookup) {
                return deps.get(lookup);
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
        }
    }
}
