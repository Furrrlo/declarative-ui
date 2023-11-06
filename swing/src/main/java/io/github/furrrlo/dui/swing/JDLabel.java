package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;

import javax.swing.*;
import java.util.function.Supplier;

public class JDLabel {

    public static DeclarativeComponent<JLabel> fn(IdentifiableConsumer<Decorator<JLabel>> body) {
        return fn(JLabel.class, JLabel::new, body);
    }

    public static DeclarativeComponent<JLabel> fn(Supplier<JLabel> factory,
                                                  IdentifiableConsumer<Decorator<JLabel>> body) {
        return fn(JLabel.class, factory, body);
    }

    public static <T extends JLabel> DeclarativeComponent<T> fn(Class<T> type,
                                                                Supplier<T> factory,
                                                                IdentifiableConsumer<Decorator<T>> body) {
        return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
    }

    public static class Decorator<T extends JLabel> extends JDComponent.Decorator<T> {

        private static final String PREFIX = "__JDLabel__";

        protected Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);
        }

        public void text(Supplier<String> text) {
            attribute(PREFIX + "text", JLabel::setText, text);
        }

        public void icon(Supplier<? extends Icon> icon) {
            attribute(PREFIX + "icon", JLabel::setIcon, icon);
        }

        public void enabled(Supplier<Boolean> enabled) {
            attribute(PREFIX + "enabled", JLabel::setEnabled, enabled);
        }
    }
}
