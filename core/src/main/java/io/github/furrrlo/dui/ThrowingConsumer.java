package io.github.furrrlo.dui;

@FunctionalInterface
public interface ThrowingConsumer<T> {

    void accept(T t) throws Exception;
}
