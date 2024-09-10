package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentityFreeConsumer;
import io.github.furrrlo.dui.IdentityFreeSupplier;
import io.github.furrrlo.dui.swing.text.JDTextComponent;

import javax.swing.*;
import javax.swing.text.EditorKit;
import java.net.URL;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDEditorPane {
  public static DeclarativeComponent<JEditorPane> fn(IdentityFreeConsumer<Decorator<JEditorPane>> body) {
    return fn(JEditorPane.class, JEditorPane::new, body);
  }

  public static DeclarativeComponent<JEditorPane> fn(Supplier<JEditorPane> factory,
      IdentityFreeConsumer<Decorator<JEditorPane>> body) {
    return fn(JEditorPane.class, factory, body);
  }

  public static <T extends JEditorPane> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentityFreeConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JEditorPane> extends JDTextComponent.Decorator<T> {
    private static final String PREFIX = "__JDEditorPane__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void contentType(IdentityFreeSupplier<String> contentType) {
      attribute(PREFIX + "contentType", JEditorPane::getContentType, JEditorPane::setContentType, contentType);
    }

    public void editorKit(IdentityFreeSupplier<? extends EditorKit> editorKit) {
      attribute(PREFIX + "editorKit", JEditorPane::getEditorKit, JEditorPane::setEditorKit, editorKit);
    }

    public void page(Supplier<URL> page) {
      // TODO: tf do I do here
      throw new UnsupportedOperationException();
//      attribute(PREFIX + "page", JEditorPane::getPage, JEditorPane::setPage, page);
    }
  }
}
