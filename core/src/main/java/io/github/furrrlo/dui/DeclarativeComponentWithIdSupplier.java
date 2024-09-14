package io.github.furrrlo.dui;

import org.jspecify.annotations.Nullable;

public interface DeclarativeComponentWithIdSupplier<T> extends DeclarativeComponentSupplier<T> {

    @Nullable String getId();
}
