package io.github.furrrlo.dui;

import java.util.Arrays;
import java.util.function.Supplier;

abstract class IdentifiableRunnable implements Runnable, Identifiable.Explicit {

    private Runnable runnable;

    public IdentifiableRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void run() {
        runnable.run();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IdentifiableRunnable)) return false;
        IdentifiableRunnable that = (IdentifiableRunnable) o;
        return Arrays.equals(deps(), that.deps());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(deps());
    }

    static IdentifiableRunnable of(Runnable runnable, Object... deps) {
        return new IdentifiableRunnable(runnable) {
            @Override
            public Object[] deps() {
                return deps;
            }
        };
    }

    static IdentifiableRunnable of(Runnable runnable, Supplier<Object[]> deps) {
        return new IdentifiableRunnable(runnable) {
            @Override
            public Object[] deps() {
                return deps.get();
            }
        };
    }
}
