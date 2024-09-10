package io.github.furrrlo.dui;

import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class IdentifiableDeps {

    static IdentifiableDeps of(Object[] deps) {
        return new IdentifiableDeps(deps);
    }

    static IdentifiableDeps immediatelyExplicit(Collection<MethodHandles.Lookup> lookups, Object[] deps) {
        return new IdentifiableDeps(lookups, deps);
    }

    private final Lock lock = new ReentrantLock();
    private volatile boolean explicitized;
    private Object[] deps;

    private IdentifiableDeps(Object[] deps) {
        this.deps = deps;
    }

    private IdentifiableDeps(Collection<MethodHandles.Lookup> lookups, Object[] deps) {
        this.deps = Identifiables.makeDependenciesExplicit(lookups, deps);
        this.explicitized = true;
    }

    Object[] get(Collection<MethodHandles.Lookup> lookups) {
        if(explicitized)
            return deps;

        lock.lock();
        try {
            if(explicitized)
                return deps;

            deps = Identifiables.makeDependenciesExplicit(lookups, deps);
            explicitized = true;
            return deps;
        } finally {
            lock.unlock();
        }
    }
}
