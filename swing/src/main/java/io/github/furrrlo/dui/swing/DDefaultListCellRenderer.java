package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;
import java.lang.Class;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.function.Supplier;
import javax.swing.DefaultListCellRenderer;

@SuppressWarnings("unused")
public class DDefaultListCellRenderer {
  public static DeclarativeComponent<DefaultListCellRenderer> fn(
      IdentifiableConsumer<Decorator<DefaultListCellRenderer>> body) {
    return fn(DefaultListCellRenderer.class, DefaultListCellRenderer::new, body);
  }

  public static DeclarativeComponent<DefaultListCellRenderer> fn(Supplier<DefaultListCellRenderer> factory,
      IdentifiableConsumer<Decorator<DefaultListCellRenderer>> body) {
    return fn(DefaultListCellRenderer.class, factory, body);
  }

  public static <T extends DefaultListCellRenderer> DeclarativeComponent<T> fn(Class<T> type,
      Supplier<T> factory, IdentifiableConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends DefaultListCellRenderer> extends JDLabel.Decorator<T> {
    private static final String PREFIX = "__DDefaultListCellRenderer__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }
  }
}
