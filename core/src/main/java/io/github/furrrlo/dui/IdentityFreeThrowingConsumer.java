package io.github.furrrlo.dui;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Collection;

public interface IdentityFreeThrowingConsumer<T> extends ThrowingConsumer<T>, IdentityFree, Serializable {

    @Override
    default Object[] deps(Collection<MethodHandles.Lookup> lookups) {
        return IdentityFrees.computeDependencies(lookups, this);
    }

    interface Explicit<T> extends IdentityFreeThrowingConsumer<T>, IdentityFree.Explicit {

        @Override
        Object[] deps(Collection<MethodHandles.Lookup> lookups);
    }

    static <T> Explicit<T> explicit(Collection<MethodHandles.Lookup> lookups, IdentityFreeThrowingConsumer<T> consumer) {
        return consumer instanceof Explicit
                ? (Explicit<T>) consumer
                : new Impl.ExplicitArray<>(lookups, consumer, consumer.deps(lookups));
    }

    static <T> Explicit<T> explicit(ThrowingConsumer<T> consumer, Object... deps) {
        return new Impl.ExplicitArray<>(consumer, deps);
    }

    static <T> Explicit<T> neverChange(ThrowingConsumer<T> consumer) {
        return new Impl.ExplicitArray<>(consumer, new Object[] {});
    }

    static <T> Explicit<T> alwaysChange(ThrowingConsumer<T> consumer) {
        return new Impl.ExplicitArray<>(consumer, new Object[] { /* Force to always change */ new Object() });
    }

    class Impl {

        private static class ExplicitArray<T> implements Explicit<T> {

            private final transient ThrowingConsumer<T> consumer;
            private final IdentityFreeDeps deps;

            public ExplicitArray(ThrowingConsumer<T> consumer, Object[] deps) {
                this.consumer = consumer;
                this.deps = IdentityFreeDeps.of(deps);
            }

            public ExplicitArray(Collection<MethodHandles.Lookup> lookups, ThrowingConsumer<T> consumer, Object[] deps) {
                this.consumer = consumer;
                this.deps = IdentityFreeDeps.immediatelyExplicit(lookups, deps);
            }

            @Override
            public void accept(T t) throws Exception {
                consumer.accept(t);
            }

            @Override
            public Object[] deps(Collection<MethodHandles.Lookup> lookups) {
                return deps.get(lookups);
            }

            @Override
            public Class<?> getImplClass() {
                return consumer.getClass();
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
                        "consumer=" + consumer +
                        ", deps=" + deps +
                        '}';
            }
        }
    }
}
