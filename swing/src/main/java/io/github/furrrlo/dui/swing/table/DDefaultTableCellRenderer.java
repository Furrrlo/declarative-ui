package io.github.furrrlo.dui.swing.table;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;
import io.github.furrrlo.dui.swing.JDLabel;
import java.awt.Color;
import java.lang.Class;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.function.Supplier;
import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings("unused")
public class DDefaultTableCellRenderer {
  public static DeclarativeComponent<DefaultTableCellRenderer> fn(
      IdentifiableConsumer<Decorator<DefaultTableCellRenderer>> body) {
    return fn(DefaultTableCellRenderer.class, DefaultTableCellRenderer::new, body);
  }

  public static DeclarativeComponent<DefaultTableCellRenderer> fn(
      Supplier<DefaultTableCellRenderer> factory,
      IdentifiableConsumer<Decorator<DefaultTableCellRenderer>> body) {
    return fn(DefaultTableCellRenderer.class, factory, body);
  }

  public static <T extends DefaultTableCellRenderer> DeclarativeComponent<T> fn(Class<T> type,
      Supplier<T> factory, IdentifiableConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends DefaultTableCellRenderer> extends JDLabel.Decorator<T> {
    private static final String PREFIX = "__DDefaultTableCellRenderer__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void background(Supplier<? extends Color> background) {
      attribute(PREFIX + "background", DefaultTableCellRenderer::setBackground, background);
    }

    public void foreground(Supplier<? extends Color> foreground) {
      attribute(PREFIX + "foreground", DefaultTableCellRenderer::setForeground, foreground);
    }
  }
}
