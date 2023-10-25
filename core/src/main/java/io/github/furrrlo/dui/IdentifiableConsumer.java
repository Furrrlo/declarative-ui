package io.github.furrrlo.dui;

import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IdentifiableConsumer<T> extends Consumer<T>, Identifiable, Serializable {

    @Override
    default Object[] deps() {
        return Identifiables.computeDependencies(this);
    }

    interface Explicit<T> extends IdentifiableConsumer<T>, Identifiable.Explicit {

        @Override
        Object[] deps();
    }

    static <T> Explicit<T> explicit(Consumer<T> consumer, Object... deps) {
        return new Impl.ExplicitArray<>(consumer, deps);
    }

    static <T> Explicit<T> explicit(Consumer<T> consumer, Supplier<Object[]> deps) {
        return new Impl.ExplicitSupplier<>(consumer, deps);
    }

    static <T> Explicit<T> alwaysChange(Consumer<T> consumer) {
        return new Impl.ExplicitArray<>(consumer, new Object[] { /* Force to always change */ new Object() });
    }

    class Impl {

        private static class ExplicitArray<T> implements Explicit<T> {

            private final transient Consumer<T> consumer;
            private final Object[] deps;

            public ExplicitArray(Consumer<T> consumer, Object[] deps) {
                this.consumer = consumer;
                this.deps = deps;
            }

            @Override
            public void accept(T t) {
                consumer.accept(t);
            }

            @Override
            public Object[] deps() {
                return deps;
            }
        }

        private static class ExplicitSupplier<T> implements Explicit<T> {

            private final transient Consumer<T> consumer;
            private final transient Supplier<Object[]> deps; // TODO: serialize the result

            public ExplicitSupplier(Consumer<T> consumer, Supplier<Object[]> deps) {
                this.consumer = consumer;
                this.deps = deps;
            }

            @Override
            public void accept(T t) {
                consumer.accept(t);
            }

            @Override
            public Object[] deps() {
                return deps.get();
            }
        }
    }
}
