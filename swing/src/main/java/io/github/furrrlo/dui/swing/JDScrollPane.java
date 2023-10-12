package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;

import javax.swing.*;
import java.util.function.Supplier;

public class JDScrollPane {

    public static DeclarativeComponent<JScrollPane> fn(IdentifiableConsumer<Decorator<JScrollPane>> body) {
        return fn(JScrollPane.class, JScrollPane::new, body);
    }

    public static DeclarativeComponent<JScrollPane> fn(Supplier<JScrollPane> factory,
                                                       IdentifiableConsumer<Decorator<JScrollPane>> body) {
        return fn(JScrollPane.class, factory, body);
    }

    public static <T extends JScrollPane> DeclarativeComponent<T> fn(Class<T> type,
                                                                     Supplier<T> factory,
                                                                     IdentifiableConsumer<Decorator<T>> body) {
        return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
    }

    public static class Decorator<T extends JScrollPane> extends JDComponent.Decorator<T> {

        private static final String PREFIX = "__JDScrollPane__";

        protected Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);
        }
    }
}
