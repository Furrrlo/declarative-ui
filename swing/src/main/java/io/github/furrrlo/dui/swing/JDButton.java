package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;

import javax.swing.*;
import java.util.function.Supplier;

public class JDButton {

    public static DeclarativeComponent<JButton> fn(DeclarativeComponent.Body<JButton, Decorator<JButton>> body) {
        return fn(JButton.class, JButton::new, body);
    }

    public static DeclarativeComponent<JButton> fn(Supplier<JButton> factory,
                                                   DeclarativeComponent.Body<JButton, Decorator<JButton>> body) {
        return fn(JButton.class, factory, body);
    }

    public static <T extends JButton> DeclarativeComponent<T> fn(Class<T> type,
                                                                 Supplier<T> factory,
                                                                 DeclarativeComponent.Body<T, Decorator<T>> body) {
        return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
    }

    public static class Decorator<T extends JButton> extends DAbstractButton.Decorator<T> {

        private static final String PREFIX = "__JDButton__";

        protected Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);
        }
    }
}
