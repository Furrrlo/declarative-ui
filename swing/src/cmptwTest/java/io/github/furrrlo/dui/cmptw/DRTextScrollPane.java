package io.github.furrrlo.dui.cmptw;

import io.github.furrrlo.dui.*;
import io.github.furrrlo.dui.swing.JDScrollPane;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;

import java.awt.*;
import java.util.function.Supplier;

class DRTextScrollPane {

    public static DeclarativeComponent<RTextScrollPane> fn(IdentityFreeConsumer<Decorator<RTextScrollPane>> body) {
        return fn(RTextScrollPane::new, body);
    }

    public static DeclarativeComponent<RTextScrollPane> fn(Supplier<RTextScrollPane> factory,
                                                           IdentityFreeConsumer<Decorator<RTextScrollPane>> body) {
        return fn(RTextScrollPane.class, factory, body);
    }

    public static <T extends RTextScrollPane> DeclarativeComponent<T> fn(Class<T> type,
                                                                         Supplier<T> factory,
                                                                         IdentityFreeConsumer<Decorator<T>> body) {
        return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
    }

    public static class Decorator<T extends RTextScrollPane> extends JDScrollPane.Decorator<T> {

        private static final String PREFIX = "__DRTextScrollPane__";

        protected Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);
        }

        @Override
        public void viewportView(DeclarativeComponentSupplier<? extends Component> viewportView) {
            fnAttribute(
                    PREFIX + "viewportView",
                    RTextScrollPane::setViewportView,
                    viewportView);
        }

        public void lineNumbersEnabled(IdentityFreeSupplier<Boolean> lineNumbersEnabled) {
            attribute(
                    PREFIX + "lineNumbersEnabled",
                    RTextScrollPane::setLineNumbersEnabled,
                    lineNumbersEnabled);
        }

        public void theme(IdentityFreeSupplier<Theme> theme) {
            attribute(
                    PREFIX + "theme",
                    (c, v) -> {
                        if (v != null && c.getTextArea() instanceof RSyntaxTextArea syntaxTextArea)
                            v.apply(syntaxTextArea);
                    },
                    theme);
        }
    }
}
