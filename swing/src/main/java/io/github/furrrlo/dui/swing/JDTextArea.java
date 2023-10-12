package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;

import javax.swing.*;
import java.util.function.Supplier;

public class JDTextArea {

    public static DeclarativeComponent<JTextArea> fn(IdentifiableConsumer<Decorator<JTextArea>> body) {
        return fn(JTextArea.class, JTextArea::new, body);
    }

    public static DeclarativeComponent<JTextArea> fn(Supplier<JTextArea> factory,
                                                     IdentifiableConsumer<Decorator<JTextArea>> body) {
        return fn(JTextArea.class, factory, body);
    }

    public static <T extends JTextArea> DeclarativeComponent<T> fn(Class<T> type,
                                                                   Supplier<T> factory,
                                                                   IdentifiableConsumer<Decorator<T>> body) {
        return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
    }

    public static class Decorator<T extends JTextArea> extends JDTextComponent.Decorator<T> {

        private static final String PREFIX = "__JDTextArea__";

        protected Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);
        }
    }
}
