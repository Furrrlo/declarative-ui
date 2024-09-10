package io.github.furrrlo.dui;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Collection;

public interface IdentityFreeThrowingRunnable extends ThrowingRunnable, IdentityFree, Serializable {

    @Override
    default Object[] deps(Collection<MethodHandles.Lookup> lookups) {
        return IdentityFrees.computeDependencies(lookups, this);
    }

    interface Explicit extends IdentityFreeThrowingRunnable, IdentityFree.Explicit {

        @Override
        Object[] deps(Collection<MethodHandles.Lookup> lookups);
    }

    static Explicit explicit(Collection<MethodHandles.Lookup> lookups, IdentityFreeThrowingRunnable runnable) {
        return runnable instanceof Explicit
                ? (Explicit) runnable
                : new Impl.ExplicitArray(lookups, runnable, runnable.deps(lookups));
    }

    static Explicit explicit(ThrowingRunnable runnable, Object... deps) {
        return new Impl.ExplicitArray(runnable, deps);
    }

    static Explicit neverChange(ThrowingRunnable runnable) {
        return new Impl.ExplicitArray(runnable, new Object[] {});
    }

    static Explicit alwaysChange(ThrowingRunnable runnable) {
        return new Impl.ExplicitArray(runnable, new Object[] { /* Force to always change */ new Object() });
    }

    class Impl {

        private static class ExplicitArray implements Explicit {

            private final transient ThrowingRunnable runnable;
            private final IdentityFreeDeps deps;

            public ExplicitArray(ThrowingRunnable runnable, Object[] deps) {
                this.runnable = runnable;
                this.deps = IdentityFreeDeps.of(deps);
            }

            public ExplicitArray(Collection<MethodHandles.Lookup> lookups, ThrowingRunnable runnable, Object[] deps) {
                this.runnable = runnable;
                this.deps = IdentityFreeDeps.immediatelyExplicit(lookups, deps);
            }

            @Override
            public void run() throws Exception {
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
    }
}
