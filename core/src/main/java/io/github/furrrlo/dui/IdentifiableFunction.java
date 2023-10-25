package io.github.furrrlo.dui;

import java.io.Serializable;
import java.util.function.Function;
import java.util.function.Supplier;

public interface IdentifiableFunction<T, R> extends Function<T, R>, Identifiable, Serializable {

    @Override
    default Object[] deps() {
        return Identifiables.computeDependencies(this);
    }

    interface Explicit<T, R> extends IdentifiableFunction<T, R>, Identifiable.Explicit {

        @Override
        Object[] deps();
    }

    static <T, R> Explicit<T, R> explicit(IdentifiableFunction<T, R> fn) {
        return fn instanceof Explicit
                ? (Explicit<T, R>) fn
                : new Impl.ExplicitArray<>(fn, fn.deps());
    }

    static <T, R> Explicit<T, R> explicit(Function<T, R> fn, Object... deps) {
        return new Impl.ExplicitArray<>(fn, deps);
    }

    static <T, R> Explicit<T, R> explicit(Function<T, R> fn, Supplier<Object[]> deps) {
        return new Impl.ExplicitSupplier<>(fn, deps);
    }

    static <T, R> Explicit<T, R> alwaysChange(Function<T, R> fn) {
        return new Impl.ExplicitArray<>(fn, new Object[] { /* Force to always change */ new Object() });
    }

    class Impl {

        private static class ExplicitArray<T, R> implements Explicit<T, R> {

            private final transient Function<T, R> fn;
            private final Object[] deps;

            public ExplicitArray(Function<T, R> fn, Object[] deps) {
                this.fn = fn;
                this.deps = deps;
            }

            @Override
            public R apply(T t) {
                return fn.apply(t);
            }

            @Override
            public Object[] deps() {
                return deps;
            }
        }

        private static class ExplicitSupplier<T, R> implements Explicit<T, R> {

            private final transient Function<T, R> fn;
            private final transient Supplier<Object[]> deps; // TODO: serialize the result

            public ExplicitSupplier(Function<T, R> fn, Supplier<Object[]> deps) {
                this.fn = fn;
                this.deps = deps;
            }

            @Override
            public R apply(T t) {
                return fn.apply(t);
            }

            @Override
            public Object[] deps() {
                return deps.get();
            }
        }
    }
}
