package io.github.furrrlo.dui;

import java.util.List;
import java.util.function.Function;

public class DWrapper {

    public static <T> DeclarativeComponent<T> fn(Function<DeclarativeComponentContext<?>, DeclarativeComponentSupplier<T>> body) {
        return DeclarativeComponentFactory.INSTANCE.wrapper(body);
    }

    public static <T> DeclarativeComponent<T> memo(
            List<Object> deps,
            Function<DeclarativeComponentContext<?>, DeclarativeComponentSupplier<T>> body) {
        return DeclarativeComponentFactory.INSTANCE.memo(deps, body);
    }
}
