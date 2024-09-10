package io.github.furrrlo.dui;

import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class IdentityFreeDeps {

    static IdentityFreeDeps of(Object[] deps) {
        return new IdentityFreeDeps(deps);
    }

    static IdentityFreeDeps immediatelyExplicit(Collection<MethodHandles.Lookup> lookups, Object[] deps) {
        return new IdentityFreeDeps(lookups, deps);
    }

    private final Lock lock = new ReentrantLock();
    private volatile boolean explicitized;
    private Object[] deps;

    private IdentityFreeDeps(Object[] deps) {
        this.deps = deps;
    }

    private IdentityFreeDeps(Collection<MethodHandles.Lookup> lookups, Object[] deps) {
        this.deps = IdentityFrees.makeDependenciesExplicit(lookups, deps);
        this.explicitized = true;
    }

    Object[] get(Collection<MethodHandles.Lookup> lookups) {
        if(explicitized)
            return deps;

        lock.lock();
        try {
            if(explicitized)
                return deps;

            deps = IdentityFrees.makeDependenciesExplicit(lookups, deps);
            explicitized = true;
            return deps;
        } finally {
            lock.unlock();
        }
    }
}
