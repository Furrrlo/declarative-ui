package io.github.furrrlo.dui;

public class DWrapper {

    public static <T> DeclarativeComponent<T> fn(
            IdentifiableFunction<DeclarativeComponentContext, DeclarativeComponentSupplier<T>> body) {
        return DeclarativeComponentFactory.INSTANCE.wrapper(body);
    }
}
