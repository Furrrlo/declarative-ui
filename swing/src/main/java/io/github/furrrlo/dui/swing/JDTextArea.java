package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;
import io.github.furrrlo.dui.IdentifiableSupplier;
import io.github.furrrlo.dui.swing.text.JDTextComponent;

import javax.swing.*;
import java.util.function.Supplier;

@SuppressWarnings("unused")
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

        public void columns(IdentifiableSupplier<Integer> columns) {
            attribute(PREFIX + "columns", JTextArea::getColumns, JTextArea::setColumns, columns);
        }

        public void lineWrap(IdentifiableSupplier<Boolean> lineWrap) {
            attribute(PREFIX + "lineWrap", JTextArea::getLineWrap, JTextArea::setLineWrap, lineWrap);
        }

        public void rows(IdentifiableSupplier<Integer> rows) {
            attribute(PREFIX + "rows", JTextArea::getRows, JTextArea::setRows, rows);
        }

        public void tabSize(IdentifiableSupplier<Integer> tabSize) {
            attribute(PREFIX + "tabSize", JTextArea::getTabSize, JTextArea::setTabSize, tabSize);
        }

        public void wrapStyleWord(IdentifiableSupplier<Boolean> wrapStyleWord) {
            attribute(PREFIX + "wrapStyleWord", JTextArea::getWrapStyleWord, JTextArea::setWrapStyleWord, wrapStyleWord);
        }
    }
}
