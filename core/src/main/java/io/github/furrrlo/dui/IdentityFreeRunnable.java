package io.github.furrrlo.dui;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.function.Supplier;

public interface IdentityFreeRunnable extends Runnable, IdentityFree, Serializable {

    @Override
    default Object[] deps(Collection<MethodHandles.Lookup> lookups) {
        return IdentityFrees.computeDependencies(lookups, this);
    }

    interface Explicit extends IdentityFreeRunnable, IdentityFree.Explicit {

        @Override
        Object[] deps(Collection<MethodHandles.Lookup> lookups);
    }

    static Explicit explicit(Collection<MethodHandles.Lookup> lookups, IdentityFreeRunnable runnable) {
        return runnable instanceof Explicit
                ? (Explicit) runnable
                : new Impl.ExplicitArray(lookups, runnable, runnable.deps(lookups));
    }

    static Explicit explicit(Runnable runnable, Object... deps) {
        return new Impl.ExplicitArray(runnable, deps);
    }

    static Explicit alwaysChange(Runnable runnable) {
        return new Impl.ExplicitArray(runnable, new Object[] { /* Force to always change */ new Object() });
    }

    static Explicit neverChange(Runnable runnable) {
        return new Impl.ExplicitArray(runnable, new Object[] {});
    }

    class Impl {

        private static class ExplicitArray implements Explicit {

            private final transient Runnable runnable;
            private final IdentityFreeDeps deps;

            public ExplicitArray(Runnable runnable, Object[] deps) {
                this.runnable = runnable;
                this.deps = IdentityFreeDeps.of(deps);
            }

            public ExplicitArray(Collection<MethodHandles.Lookup> lookups, Runnable runnable, Object[] deps) {
                this.runnable = runnable;
                this.deps = IdentityFreeDeps.immediatelyExplicit(lookups, deps);
            }

            @Override
            public void run() {
                runnable.run();
            }

            @Override
            public Object[] deps(Collection<MethodHandles.Lookup> lookups) {
                return deps.get(lookups);
            }

            @Override
            public Class<?> getImplClass() {
                return runnable.getClass();
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

        public static Explicit explicit(Runnable runnable, Supplier<Object[]> deps) {
            return new Impl.ExplicitSupplier(runnable, deps);
        }

        private static class ExplicitSupplier implements Explicit {

            private final transient Runnable runnable;
            private final transient Supplier<Object[]> deps; // TODO: serialize the result

            public ExplicitSupplier(Runnable runnable, Supplier<Object[]> deps) {
                this.runnable = runnable;
                this.deps = deps;
            }

            @Override
            public void run() {
                runnable.run();
            }

            @Override
            public Object[] deps(Collection<MethodHandles.Lookup> lookups) {
                return IdentityFrees.makeDependenciesExplicit(lookups, deps.get());
            }

            @Override
            public Class<?> getImplClass() {
                return runnable.getClass();
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
