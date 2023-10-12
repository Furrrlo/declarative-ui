package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;

import javax.swing.*;
import java.util.function.Supplier;

public class JDButton {

    public static DeclarativeComponent<JButton> fn(IdentifiableConsumer<Decorator<JButton>> body) {
        return fn(JButton.class, JButton::new, body);
    }

    public static DeclarativeComponent<JButton> fn(Supplier<JButton> factory, IdentifiableConsumer<Decorator<JButton>> body) {
        return fn(JButton.class, factory, body);
    }

    public static <T extends JButton> DeclarativeComponent<T> fn(Class<T> type,
                                                                 Supplier<T> factory,
                                                                 IdentifiableConsumer<Decorator<T>> body) {
        return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
    }

    public static class Decorator<T extends JButton> extends DAbstractButton.Decorator<T> {

        private static final String PREFIX = "__JDButton__";

        protected Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);
        }
    }
}
