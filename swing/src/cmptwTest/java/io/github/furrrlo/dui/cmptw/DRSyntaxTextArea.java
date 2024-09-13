package io.github.furrrlo.dui.cmptw;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentityFreeConsumer;
import io.github.furrrlo.dui.IdentityFreeSupplier;
import io.github.furrrlo.dui.swing.JDTextArea;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;

import java.util.Objects;
import java.util.function.Supplier;

class DRSyntaxTextArea {

    public static DeclarativeComponent<RSyntaxTextArea> fn(IdentityFreeConsumer<Decorator<RSyntaxTextArea>> body) {
        return fn(RSyntaxTextArea.class, RSyntaxTextArea::new, body);
    }

    public static DeclarativeComponent<RSyntaxTextArea> fn(Supplier<RSyntaxTextArea> factory,
                                                           IdentityFreeConsumer<Decorator<RSyntaxTextArea>> body) {
        return fn(RSyntaxTextArea.class, factory, body);
    }

    public static <T extends RSyntaxTextArea> DeclarativeComponent<T> fn(Class<T> type,
                                                                         Supplier<T> factory,
                                                                         IdentityFreeConsumer<Decorator<T>> body) {
        return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
    }

    public static class Decorator<T extends RSyntaxTextArea> extends JDTextArea.Decorator<T> {

        private static final String PREFIX = "__DRSyntaxTextArea__";

        public Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);
        }

        @Override
        public void text(IdentityFreeSupplier<String> text) {
            attribute(
                    PREFIX + "text",
                    (c, v) -> {
                        if (!Objects.equals(c.getText(), v)) {
                            c.discardAllEdits();
                            c.setText(v);
                        }
                    },
                    text);
        }

        public void codeFoldingEnabled(IdentityFreeSupplier<Boolean> enabled) {
            attribute(
                    PREFIX + "codeFoldingEnabled",
                    RSyntaxTextArea::setCodeFoldingEnabled,
                    enabled);
        }

        public void syntaxEditingStyle(IdentityFreeSupplier<String> style) {
            attribute(
                    PREFIX + "syntaxEditingStyle",
                    RSyntaxTextArea::setSyntaxEditingStyle,
                    style);
        }

        public void autoCompletion(IdentityFreeSupplier<AutoCompletion> autoCompletion) {
            attribute(
                    PREFIX + "autoCompletion",
                    (c, v) -> {
                        if (v != null)
                            v.install(c);
                    },
                    autoCompletion);
        }

        public void theme(IdentityFreeSupplier<Theme> theme) {
            attribute(
                    PREFIX + "theme",
                    (c, v) -> {
                        if (v != null)
                            v.apply(c);
                    },
                    theme);
        }
    }
}
