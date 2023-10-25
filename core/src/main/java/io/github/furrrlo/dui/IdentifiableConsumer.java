package io.github.furrrlo.dui;

import java.io.Serializable;
import java.util.function.Consumer;

public interface IdentifiableConsumer<T> extends Consumer<T>, Identifiable, Serializable {

    @Override
    default Object[] deps() {
        return Identifiables.computeDependencies(this);
    }

    interface Explicit<T> extends IdentifiableConsumer<T>, Identifiable.Explicit {

        @Override
        Object[] deps();
    }

    static <T> Explicit<T> explicit(IdentifiableConsumer<T> consumer) {
        return consumer instanceof Explicit
                ? (Explicit<T>) consumer
                : new Impl.ExplicitArray<>(consumer, consumer.deps());
    }

    static <T> Explicit<T> explicit(Consumer<T> consumer, Object... deps) {
        return new Impl.ExplicitArray<>(consumer, deps);
    }

    static <T> Explicit<T> neverChange(Consumer<T> consumer) {
        return new Impl.ExplicitArray<>(consumer, new Object[] {});
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
                this.deps = Identifiables.makeDependenciesExplicit(deps);
            }

            @Override
            public void accept(T t) {
                consumer.accept(t);
            }

            @Override
            public Object[] deps() {
                return deps;
            }

            @Override
            public Class<?> getImplClass() {
                return consumer.getClass();
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
