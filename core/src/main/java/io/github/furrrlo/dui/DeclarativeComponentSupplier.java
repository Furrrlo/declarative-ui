package io.github.furrrlo.dui;

import java.util.function.Function;

@FunctionalInterface
public interface DeclarativeComponentSupplier<T> extends Function<DeclarativeComponentFactory, DeclarativeComponent<T>> {

    default DeclarativeComponent<T> doApply() {
        if(this instanceof DeclarativeComponent)
            return (DeclarativeComponent<T>) this;
        if(this instanceof FactoryLess)
            return ((FactoryLess<T>) this).apply();
        return apply(DeclarativeComponentFactory.INSTANCE);
    }

    default StatefulDeclarativeComponent<T, ?, ?> doApplyInternal() {
        final DeclarativeComponent<T> declarative = doApply();
        if(!(declarative instanceof StatefulDeclarativeComponent))
            throw new UnsupportedOperationException("Unexpected declarative component " + declarative);
        return (StatefulDeclarativeComponent<T, ?, ?>) declarative;
    }

    interface FactoryLess<T> extends DeclarativeComponentSupplier<T> {

        DeclarativeComponent<T> apply();

        @Override
        default DeclarativeComponent<T> apply(DeclarativeComponentFactory declarativeComponentFactory) {
            return apply();
        }
    }
}
