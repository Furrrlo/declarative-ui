package io.github.furrrlo.dui;

public interface DeclarativeComponent<T> extends DeclarativeComponentSupplier<T> {

    @Override
    default DeclarativeComponent<T> apply(DeclarativeComponentFactory declarativeComponentFactory) {
        return this;
    }

    interface Body<T, C extends DeclarativeComponentContext<T>> {

        void component(C ctx);
    }
}
