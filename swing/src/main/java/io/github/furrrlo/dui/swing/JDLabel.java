package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;

import javax.swing.*;
import java.util.function.Supplier;

public class JDLabel {

    public static DeclarativeComponent<JLabel> fn(DeclarativeComponent.Body<JLabel, Decorator<JLabel>> body) {
        return fn(JLabel.class, JLabel::new, body);
    }

    public static DeclarativeComponent<JLabel> fn(Supplier<JLabel> factory,
                                                  DeclarativeComponent.Body<JLabel, Decorator<JLabel>> body) {
        return fn(JLabel.class, factory, body);
    }

    public static <T extends JLabel> DeclarativeComponent<T> fn(Class<T> type,
                                                                Supplier<T> factory,
                                                                DeclarativeComponent.Body<T, Decorator<T>> body) {
        return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
    }

    public static class Decorator<T extends JLabel> extends JDComponent.Decorator<T> {

        private static final String PREFIX = "__JDLabel__";

        protected Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);
        }

        public void text(String text) {
            attribute(PREFIX + "text", JLabel::setText, text);
        }

        public void icon(Icon icon) {
            attribute(PREFIX + "icon", JLabel::setIcon, icon);
        }

        public void enabled(boolean enabled) {
            attribute(PREFIX + "enabled", JLabel::setEnabled, enabled);
        }
    }
}
