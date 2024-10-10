package io.github.furrrlo.dui;

import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public class DeclarativeComponentFactory {

    public static DeclarativeComponentFactory INSTANCE = new DeclarativeComponentFactory();

    private DeclarativeComponentFactory() {
    }

    public <T, C extends DeclarativeComponentContextDecorator<T>> DeclarativeComponent<T> of(Supplier<C> decoratorFactory) {
        return of(decoratorFactory, null);
    }

    public <T, C extends DeclarativeComponentContextDecorator<T>> DeclarativeComponent<T> of(Supplier<C> decoratorFactory,
                                                                                             @Nullable IdentityFreeConsumer<C> body) {
        return new DeclarativeComponentImpl<>(decoratorFactory, body);
    }

    <T, C extends DeclarativeComponentContextDecorator<T>> DeclarativeComponent<T> ofApplication(ApplicationConfig appConfig,
                                                                                                 Supplier<C> decoratorFactory,
                                                                                                 @Nullable IdentityFreeConsumer<C> body) {
        return new DeclarativeComponentImpl<>(appConfig, decoratorFactory, body);
    }

    public <T> DeclarativeComponent<T> wrapper(IdentityFreeFunction<DeclarativeComponentContext, DeclarativeComponentSupplier<? extends T>> body) {
        return new DeclarativeComponentWrapper<>(body);
    }
}
