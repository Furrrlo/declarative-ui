package io.github.furrrlo.dui.swing.text;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentityFreeConsumer;
import io.github.furrrlo.dui.swing.EventListenerWrapper;
import io.github.furrrlo.dui.swing.SwingDecorator;
import io.leangen.geantyref.TypeToken;

import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import java.util.EventListener;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class DDocument {

    public static DeclarativeComponent<Document> forInner(IdentityFreeConsumer<Decorator<Document>> body) {
        return DeclarativeComponentFactory.INSTANCE.of(
                () -> new Decorator<>(Document.class, () -> {
                    throw new IllegalStateException("This AccessibleContext was built for a inner component");
                }),
                body);
    }

    public static class Decorator<T extends Document> extends SwingDecorator<T> {

        private static final String PREFIX = "__DDocument__";

        protected Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);
        }

        protected Decorator(TypeToken<T> type, Supplier<T> factory) {
            super(type, factory);
        }

        public void documentListener(DocumentListener documentListener) {
            eventListener(
                    PREFIX + "documentListener",
                    DocumentListenerWrapper::new,
                    Document::addDocumentListener,
                    documentListener);
        }

        public void undoableEditListener(UndoableEditListener undoableEditListener) {
            eventListener(
                    PREFIX + "documentListener",
                    UndoableEditListenerWrapper::new,
                    Document::addUndoableEditListener,
                    undoableEditListener);
        }

        @SuppressWarnings("unchecked")
        public <L extends EventListener> void eventListener(String key,
                                                            Function<L, EventListenerWrapper<L>> factory,
                                                            BiConsumer<T, L> adder,
                                                            L l) {
            attribute(
                    key,
                    (document, v) -> {
                        Object prev = document.getProperty(key);
                        if (prev instanceof EventListenerWrapper)
                            ((EventListenerWrapper<L>) prev).setWrapped(v);
                        else
                            adder.accept(document, (L) factory.apply(v));
                    },
                    () -> l);
        }
    }
}
