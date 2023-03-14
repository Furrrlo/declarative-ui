package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;

import javax.swing.*;
import java.util.function.Supplier;

public class JDPanel {

    public static DeclarativeComponent<JPanel> fn(DeclarativeComponent.Body<JPanel, Decorator<JPanel>> body) {
        return fn(JPanel::new, body);
    }

    public static DeclarativeComponent<JPanel> fn(Supplier<JPanel> factory,
                                                  DeclarativeComponent.Body<JPanel, Decorator<JPanel>> body) {
        return fn(JPanel.class, factory, body);
    }

    public static <T extends JPanel> DeclarativeComponent<T> fn(Class<T> type,
                                                                Supplier<T> factory,
                                                                DeclarativeComponent.Body<T, Decorator<T>> body) {
        return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
    }

    public static class Decorator<T extends JPanel> extends JDComponent.Decorator<T> {

        private static final String PREFIX = "__JDPanel__";

        protected Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);
        }
    }
}
