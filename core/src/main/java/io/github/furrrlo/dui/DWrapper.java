package io.github.furrrlo.dui;

public class DWrapper {

    public static <T> DeclarativeComponent<T> fn(
            IdentityFreeFunction<DeclarativeComponentContext, DeclarativeComponentSupplier<? extends T>> body) {
        return DeclarativeComponentFactory.INSTANCE.wrapper(body);
    }
}
