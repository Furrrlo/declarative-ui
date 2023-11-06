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
    
public void action(java.util.function.Supplier<? extends javax.swing.Action> action) {
  attribute(PREFIX + "action", javax.swing.JTextField::getAction, javax.swing.JTextField::setAction, action);
}

public void actionCommand(java.util.function.Supplier<java.lang.String> actionCommand) {
  attribute(PREFIX + "actionCommand", javax.swing.JTextField::setActionCommand, actionCommand);
}

public void columns(java.util.function.Supplier<java.lang.Integer> columns) {
  attribute(PREFIX + "columns", javax.swing.JTextField::getColumns, javax.swing.JTextField::setColumns, columns);
}

public void document(java.util.function.Supplier<? extends javax.swing.text.Document> document) {
  attribute(PREFIX + "document", javax.swing.JTextField::setDocument, document);
}

public void font(java.util.function.Supplier<? extends java.awt.Font> font) {
  attribute(PREFIX + "font", javax.swing.JTextField::setFont, font);
}

public void horizontalAlignment(
    java.util.function.Supplier<java.lang.Integer> horizontalAlignment) {
  attribute(PREFIX + "horizontalAlignment", javax.swing.JTextField::getHorizontalAlignment, javax.swing.JTextField::setHorizontalAlignment, horizontalAlignment);
}

public void scrollOffset(java.util.function.Supplier<java.lang.Integer> scrollOffset) {
  attribute(PREFIX + "scrollOffset", javax.swing.JTextField::getScrollOffset, javax.swing.JTextField::setScrollOffset, scrollOffset);
}
}
}
