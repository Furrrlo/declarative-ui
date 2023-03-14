package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;

import javax.swing.*;
import java.util.function.Supplier;

public class JDTextArea {

    public static DeclarativeComponent<JTextArea> fn(
            DeclarativeComponent.Body<JTextArea, Decorator<JTextArea>> body) {
        return fn(JTextArea.class, JTextArea::new, body);
    }

    public static DeclarativeComponent<JTextArea> fn(Supplier<JTextArea> factory,
                                                     DeclarativeComponent.Body<JTextArea, Decorator<JTextArea>> body) {
        return fn(JTextArea.class, factory, body);
    }

    public static <T extends JTextArea> DeclarativeComponent<T> fn(Class<T> type,
                                                                   Supplier<T> factory,
                                                                   DeclarativeComponent.Body<T, Decorator<T>> body) {
        return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
    }

    public static class Decorator<T extends JTextArea> extends JDTextComponent.Decorator<T> {

        private static final String PREFIX = "__JDTextArea__";

        protected Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);
        }
    }
}
