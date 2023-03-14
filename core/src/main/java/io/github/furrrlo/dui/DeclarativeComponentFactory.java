package io.github.furrrlo.dui;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class DeclarativeComponentFactory {

    public static DeclarativeComponentFactory INSTANCE = new DeclarativeComponentFactory();

    private DeclarativeComponentFactory() {
    }

    public <T> DeclarativeComponent<T> of(Class<T> childType, Supplier<T> factory) {
        return of(childType, factory, null);
    }

    public <T> DeclarativeComponent<T> of(Class<T> childType,
                                          Supplier<T> factory,
                                          @Nullable DeclarativeComponent.Body<T, DeclarativeComponentContext<T>> body) {
        return new DeclarativeComponentImpl<>(childType, factory, body);
    }

    public <T, C extends DeclarativeComponentContextDecorator<T>> DeclarativeComponent<T> of(Supplier<C> decoratorFactory) {
        return of(decoratorFactory, null);
    }

    public <T, C extends DeclarativeComponentContextDecorator<T>> DeclarativeComponent<T> of(Supplier<C> decoratorFactory,
                                                                                             @Nullable DeclarativeComponent.Body<T, C> body) {
        return new DeclarativeComponentImpl<>(decoratorFactory, body);
    }

    public <T> DeclarativeComponent<T> wrapper(Function<DeclarativeComponentContext<?>, DeclarativeComponentSupplier<T>> body) {
        return new DeclarativeComponentWrapper<>(body);
    }

    public <T> DeclarativeComponent<T> memo(List<Object> deps,
                                            Function<DeclarativeComponentContext<?>, DeclarativeComponentSupplier<T>> body) {
        return new DeclarativeComponentWrapper<>(deps, body);
    }
}
