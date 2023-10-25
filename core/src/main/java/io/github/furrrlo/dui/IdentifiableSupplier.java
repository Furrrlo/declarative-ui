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

    static <T> Explicit<T> explicit(Supplier<T> supplier, Object... deps) {
        return new Impl.ExplicitArray<>(supplier, deps);
    }

    static <T> Explicit<T> explicit(Supplier<T> supplier, Supplier<Object[]> deps) {
        return new Impl.ExplicitSupplier<>(supplier, deps);
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
                this.deps = deps;
            }

            @Override
            public T get() {
                return supplier.get();
            }

            @Override
            public Object[] deps() {
                return deps;
            }
        }

        private static class ExplicitSupplier<T> implements Explicit<T> {

            private final transient Supplier<T> supplier;
            private final transient Supplier<Object[]> deps; // TODO: serialize the result

            public ExplicitSupplier(Supplier<T> supplier, Supplier<Object[]> deps) {
                this.supplier = supplier;
                this.deps = deps;
            }

            @Override
            public T get() {
                return supplier.get();
            }

            @Override
            public Object[] deps() {
                return deps.get();
            }
        }
    }
}
