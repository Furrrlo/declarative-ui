package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;

import javax.swing.*;
import java.util.function.Supplier;

public class JDTextField {

    public static DeclarativeComponent<JTextField> fn(IdentifiableConsumer<Decorator<JTextField>> body) {
        return fn(JTextField.class, JTextField::new, body);
    }

    public static DeclarativeComponent<JTextField> fn(Supplier<JTextField> factory,
                                                      IdentifiableConsumer<Decorator<JTextField>> body) {
        return fn(JTextField.class, factory, body);
    }

    public static <T extends JTextField> DeclarativeComponent<T> fn(Class<T> type,
                                                                    Supplier<T> factory,
                                                                    IdentifiableConsumer<Decorator<T>> body) {
        return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
    }

    public static class Decorator<T extends JTextField> extends JDTextComponent.Decorator<T> {

        private static final String PREFIX = "__JDTextField__";

        protected Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);
        }
    }
}
