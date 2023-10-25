package io.github.furrrlo.dui;

import java.io.Serializable;
import java.util.function.Supplier;

public interface IdentifiableSupplier<T> extends Supplier<T>, Identifiable, Serializable {

    @Override
    default Object[] deps() {
        return Identifiables.computeDependencies(this);
    }

    interface Explicit<T> extends IdentifiableSupplier<T>, Identifiable.Explicit {

        @Override
        Object[] deps();
    }

    static <T> Explicit<T> explicit(IdentifiableSupplier<T> supplier) {
        return supplier instanceof Explicit
                ? (Explicit<T>) supplier
                : new Impl.ExplicitArray<>(supplier, supplier.deps());
    }

    static <T> Explicit<T> explicit(Supplier<T> supplier, Object... deps) {
        return new Impl.ExplicitArray<>(supplier, deps);
    }

    static <T> Explicit<T> alwaysChange(Supplier<T> supplier) {
        return new Impl.ExplicitArray<>(supplier, new Object[] { /* Force to always change */ new Object() });
    }

    class Impl {

        private static class ExplicitArray<T> implements Explicit<T> {

            private final transient Supplier<T> supplier;
            private final Object[] deps;

            public ExplicitArray(Supplier<T> supplier, Object[] deps) {
                this.supplier = supplier;
                this.deps = Identifiables.makeDependenciesExplicit(deps);
            }

            @Override
            public T get() {
                return supplier.get();
            }

            @Override
            public Object[] deps() {
                return deps;
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
