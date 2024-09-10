package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentityFreeConsumer;
import io.github.furrrlo.dui.IdentityFreeSupplier;

import javax.swing.*;
import javax.swing.plaf.PanelUI;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDPanel {

    public static DeclarativeComponent<JPanel> fn(IdentityFreeConsumer<Decorator<JPanel>> body) {
        return fn(JPanel::new, body);
    }

    public static DeclarativeComponent<JPanel> fn(Supplier<JPanel> factory,
                                                  IdentityFreeConsumer<Decorator<JPanel>> body) {
        return fn(JPanel.class, factory, body);
    }

    public static <T extends JPanel> DeclarativeComponent<T> fn(Class<T> type,
                                                                Supplier<T> factory,
                                                                IdentityFreeConsumer<Decorator<T>> body) {
        return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
    }

    public static class Decorator<T extends JPanel> extends JDComponent.Decorator<T> {

        private static final String PREFIX = "__JDPanel__";

        protected Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);
        }

        public void ui(IdentityFreeSupplier<? extends PanelUI> ui) {
            attribute(PREFIX + "ui", JPanel::getUI, JPanel::setUI, ui);
        }
    }
}
