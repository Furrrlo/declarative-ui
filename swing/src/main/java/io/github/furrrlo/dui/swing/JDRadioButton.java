package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentityFreeConsumer;

import javax.swing.*;
import java.util.function.Supplier;

public class JDRadioButton {

    public static DeclarativeComponent<JRadioButton> fn(IdentityFreeConsumer<Decorator<JRadioButton>> body) {
        return fn(JRadioButton.class, JRadioButton::new, body);
    }

    public static DeclarativeComponent<JRadioButton> fn(Supplier<JRadioButton> factory,
                                                        IdentityFreeConsumer<Decorator<JRadioButton>> body) {
        return fn(JRadioButton.class, factory, body);
    }

    public static <T extends JRadioButton> DeclarativeComponent<T> fn(Class<T> type,
                                                                      Supplier<T> factory,
                                                                      IdentityFreeConsumer<Decorator<T>> body) {
        return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
    }

    public static class Decorator<T extends JRadioButton> extends DAbstractButton.Decorator<T> {

        @SuppressWarnings("unused")
        private static final String PREFIX = "__JDRadioButton__";

        protected Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);
        }
    }
}
