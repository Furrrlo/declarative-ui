package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentityFreeConsumer;
import io.github.furrrlo.dui.IdentityFreeSupplier;
import io.github.furrrlo.dui.swing.text.JDTextComponent;

import javax.swing.*;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDTextField {

    public static DeclarativeComponent<JTextField> fn(IdentityFreeConsumer<Decorator<JTextField>> body) {
        return fn(JTextField.class, JTextField::new, body);
    }

    public static DeclarativeComponent<JTextField> fn(Supplier<JTextField> factory,
                                                      IdentityFreeConsumer<Decorator<JTextField>> body) {
        return fn(JTextField.class, factory, body);
    }

    public static <T extends JTextField> DeclarativeComponent<T> fn(Class<T> type,
                                                                    Supplier<T> factory,
                                                                    IdentityFreeConsumer<Decorator<T>> body) {
        return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
    }

    public static class Decorator<T extends JTextField> extends JDTextComponent.Decorator<T> {

        private static final String PREFIX = "__JDTextField__";

        protected Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);
        }

        public void action(IdentityFreeSupplier<? extends Action> action) {
            attribute(PREFIX + "action", JTextField::getAction, JTextField::setAction, action);
        }

        public void actionCommand(IdentityFreeSupplier<String> actionCommand) {
            attribute(PREFIX + "actionCommand", JTextField::setActionCommand, actionCommand);
        }

        public void columns(IdentityFreeSupplier<Integer> columns) {
            attribute(PREFIX + "columns", JTextField::getColumns, JTextField::setColumns, columns);
        }

        public void horizontalAlignment(IdentityFreeSupplier<Integer> horizontalAlignment) {
            attribute(PREFIX + "horizontalAlignment", JTextField::getHorizontalAlignment, JTextField::setHorizontalAlignment, horizontalAlignment);
        }

        public void scrollOffset(IdentityFreeSupplier<Integer> scrollOffset) {
            attribute(PREFIX + "scrollOffset", JTextField::getScrollOffset, JTextField::setScrollOffset, scrollOffset);
        }
    }
}
