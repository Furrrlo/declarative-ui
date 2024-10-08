package io.github.furrrlo.dui;

import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public class NoFrameworkDecorator<T> extends DeclarativeComponentContextDecorator<T> {

    protected NoFrameworkDecorator(@Nullable Class<T> type, Supplier<@Nullable T> factory) {
        super(type, factory, () -> true, new FrameworkScheduler(Runnable::run));
    }
}
