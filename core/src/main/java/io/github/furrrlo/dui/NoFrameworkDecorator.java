package io.github.furrrlo.dui;

import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class NoFrameworkDecorator<T> extends DeclarativeComponentContextDecorator<T> {

    protected NoFrameworkDecorator(@Nullable Class<T> type, Supplier<@Nullable T> factory) {
        super(type, factory, () -> true, Runnable::run);
    }
}
