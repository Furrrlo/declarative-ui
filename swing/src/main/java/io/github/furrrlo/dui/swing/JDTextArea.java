package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;

import javax.swing.*;
import java.util.function.Supplier;

public class JDTextArea {

    public static DeclarativeComponent<JTextArea> fn(IdentifiableConsumer<Decorator<JTextArea>> body) {
        return fn(JTextArea.class, JTextArea::new, body);
    }

    public static DeclarativeComponent<JTextArea> fn(Supplier<JTextArea> factory,
                                                     IdentifiableConsumer<Decorator<JTextArea>> body) {
        return fn(JTextArea.class, factory, body);
    }

    public static <T extends JTextArea> DeclarativeComponent<T> fn(Class<T> type,
                                                                   Supplier<T> factory,
                                                                   IdentifiableConsumer<Decorator<T>> body) {
        return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
    }

    public static class Decorator<T extends JTextArea> extends JDTextComponent.Decorator<T> {

        private static final String PREFIX = "__JDTextArea__";

        protected Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);
        }
    
public void columns(java.util.function.Supplier<java.lang.Integer> columns) {
  attribute(PREFIX + "columns", javax.swing.JTextArea::getColumns, javax.swing.JTextArea::setColumns, columns);
}

public void font(java.util.function.Supplier<? extends java.awt.Font> font) {
  attribute(PREFIX + "font", javax.swing.JTextArea::setFont, font);
}

public void lineWrap(java.util.function.Supplier<java.lang.Boolean> lineWrap) {
  attribute(PREFIX + "lineWrap", javax.swing.JTextArea::getLineWrap, javax.swing.JTextArea::setLineWrap, lineWrap);
}

public void rows(java.util.function.Supplier<java.lang.Integer> rows) {
  attribute(PREFIX + "rows", javax.swing.JTextArea::getRows, javax.swing.JTextArea::setRows, rows);
}

public void tabSize(java.util.function.Supplier<java.lang.Integer> tabSize) {
  attribute(PREFIX + "tabSize", javax.swing.JTextArea::getTabSize, javax.swing.JTextArea::setTabSize, tabSize);
}

public void wrapStyleWord(java.util.function.Supplier<java.lang.Boolean> wrapStyleWord) {
  attribute(PREFIX + "wrapStyleWord", javax.swing.JTextArea::getWrapStyleWord, javax.swing.JTextArea::setWrapStyleWord, wrapStyleWord);
}
}
}
