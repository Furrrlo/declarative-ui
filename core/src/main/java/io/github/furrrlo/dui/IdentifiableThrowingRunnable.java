package io.github.furrrlo.dui;

import java.io.Serializable;

public interface IdentifiableThrowingRunnable extends ThrowingRunnable, Identifiable, Serializable {

    @Override
    default Object[] deps() {
        return Identifiables.computeDependencies(this);
    }

    interface Explicit extends IdentifiableThrowingRunnable, Identifiable.Explicit {

        @Override
        Object[] deps();
    }

    static Explicit explicit(IdentifiableThrowingRunnable runnable) {
        return runnable instanceof Explicit
                ? (Explicit) runnable
                : new Impl.ExplicitArray(runnable, runnable.deps());
    }

    static Explicit explicit(ThrowingRunnable runnable, Object... deps) {
        return new Impl.ExplicitArray(runnable, deps);
    }

    static Explicit alwaysChange(ThrowingRunnable runnable) {
        return new Impl.ExplicitArray(runnable, new Object[] { /* Force to always change */ new Object() });
    }

    class Impl {

        private static class ExplicitArray implements Explicit {

            private final transient ThrowingRunnable runnable;
            private final Object[] deps;

            public ExplicitArray(ThrowingRunnable runnable, Object[] deps) {
                this.runnable = runnable;
                this.deps = Identifiables.makeDependenciesExplicit(deps);
            }

            @Override
            public void run() throws Exception {
                runnable.run();
            }

            @Override
            public Object[] deps() {
                return deps;
            }

            @Override
            public Class<?> getImplClass() {
                return runnable.getClass();
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
