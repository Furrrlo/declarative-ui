package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;
import io.github.furrrlo.dui.swing.text.JDTextComponent;

import javax.swing.*;
import javax.swing.text.Document;
import java.util.function.Supplier;

@SuppressWarnings("unused")
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

        public void action(Supplier<? extends Action> action) {
            attribute(PREFIX + "action", JTextField::getAction, JTextField::setAction, action);
        }

        public void actionCommand(Supplier<String> actionCommand) {
            attribute(PREFIX + "actionCommand", JTextField::setActionCommand, actionCommand);
        }

        public void columns(Supplier<Integer> columns) {
            attribute(PREFIX + "columns", JTextField::getColumns, JTextField::setColumns, columns);
        }

        public void document(Supplier<? extends Document> document) {
            attribute(PREFIX + "document", JTextField::setDocument, document);
        }

        public void horizontalAlignment(
                Supplier<Integer> horizontalAlignment) {
            attribute(PREFIX + "horizontalAlignment", JTextField::getHorizontalAlignment, JTextField::setHorizontalAlignment, horizontalAlignment);
        }

        public void scrollOffset(Supplier<Integer> scrollOffset) {
            attribute(PREFIX + "scrollOffset", JTextField::getScrollOffset, JTextField::setScrollOffset, scrollOffset);
        }
    }
}
