package io.github.furrrlo.dui;

import java.util.function.Consumer;

public interface DeclarativeRefComponentContext<T> extends DeclarativeComponentContext<T> {

    void ref(Ref<? super T> ref);

    void ref(Consumer<? super T> ref);
}
