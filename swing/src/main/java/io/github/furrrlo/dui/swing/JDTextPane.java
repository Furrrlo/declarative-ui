package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;

import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDTextPane {
  public static DeclarativeComponent<JTextPane> fn(IdentifiableConsumer<Decorator<JTextPane>> body) {
    return fn(JTextPane.class, JTextPane::new, body);
  }

  public static DeclarativeComponent<JTextPane> fn(Supplier<JTextPane> factory,
      IdentifiableConsumer<Decorator<JTextPane>> body) {
    return fn(JTextPane.class, factory, body);
  }

  public static <T extends JTextPane> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentifiableConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JTextPane> extends JDEditorPane.Decorator<T> {
    private static final String PREFIX = "__JDTextPane__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void document(Supplier<? extends Document> document) {
      attribute(PREFIX + "document", JTextPane::setDocument, document);
    }

    public void editorKit(Supplier<? extends EditorKit> editorKit) {
      attribute(PREFIX + "editorKit", JTextPane::setEditorKit, editorKit);
    }

    public void logicalStyle(Supplier<? extends Style> logicalStyle) {
      attribute(PREFIX + "logicalStyle", JTextPane::getLogicalStyle, JTextPane::setLogicalStyle, logicalStyle);
    }

    public void styledDocument(Supplier<? extends StyledDocument> styledDocument) {
      attribute(PREFIX + "styledDocument", JTextPane::getStyledDocument, JTextPane::setStyledDocument, styledDocument);
    }
  }
}
