package io.github.furrrlo.dui;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.function.Supplier;

public interface IdentifiableSupplier<T> extends Supplier<T>, Identifiable, Serializable {

    @Override
    default Object[] deps(Collection<MethodHandles.Lookup> lookups) {
        return Identifiables.computeDependencies(lookups, this);
    }

    interface Explicit<T> extends IdentifiableSupplier<T>, Identifiable.Explicit {

        @Override
        Object[] deps(Collection<MethodHandles.Lookup> lookups);
    }

    static <T> Explicit<T> explicit(Collection<MethodHandles.Lookup> lookups, IdentifiableSupplier<T> supplier) {
        return supplier instanceof Explicit
                ? (Explicit<T>) supplier
                : new Impl.ExplicitArray<>(lookups, supplier, supplier.deps(lookups));
    }

    static <T> Explicit<T> explicit(Supplier<T> supplier, Object... deps) {
        return new Impl.ExplicitArray<>(supplier, deps);
    }

    static <T> Explicit<T> neverChange(Supplier<T> supplier) {
        return new Impl.ExplicitArray<>(supplier, new Object[] {});
    }

    static <T> Explicit<T> alwaysChange(Supplier<T> supplier) {
        return new Impl.ExplicitArray<>(supplier, new Object[] { /* Force to always change */ new Object() });
    }

    class Impl {

        private static class ExplicitArray<T> implements Explicit<T> {

            private final transient Supplier<T> supplier;
            private final IdentifiableDeps deps;

            public ExplicitArray(Supplier<T> supplier, Object[] deps) {
                this.supplier = supplier;
                this.deps = IdentifiableDeps.of(deps);
            }

            public ExplicitArray(Collection<MethodHandles.Lookup> lookups, Supplier<T> supplier, Object[] deps) {
                this.supplier = supplier;
                this.deps = IdentifiableDeps.immediatelyExplicit(lookups, deps);
            }

            @Override
            public T get() {
                return supplier.get();
            }

            @Override
            public Object[] deps(Collection<MethodHandles.Lookup> lookups) {
                return deps.get(lookups);
            }

            @Override
            public Class<?> getImplClass() {
                return supplier.getClass();
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
