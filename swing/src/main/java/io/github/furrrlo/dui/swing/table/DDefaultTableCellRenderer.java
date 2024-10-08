package io.github.furrrlo.dui.swing.table;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentityFreeConsumer;
import io.github.furrrlo.dui.swing.JDLabel;

import javax.swing.table.DefaultTableCellRenderer;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class DDefaultTableCellRenderer {
  public static DeclarativeComponent<DefaultTableCellRenderer> fn(
      IdentityFreeConsumer<Decorator<DefaultTableCellRenderer>> body) {
    return fn(DefaultTableCellRenderer.class, DefaultTableCellRenderer::new, body);
  }

  public static DeclarativeComponent<DefaultTableCellRenderer> fn(
      Supplier<DefaultTableCellRenderer> factory,
      IdentityFreeConsumer<Decorator<DefaultTableCellRenderer>> body) {
    return fn(DefaultTableCellRenderer.class, factory, body);
  }

  public static <T extends DefaultTableCellRenderer> DeclarativeComponent<T> fn(Class<T> type,
      Supplier<T> factory, IdentityFreeConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends DefaultTableCellRenderer> extends JDLabel.Decorator<T> {
    private static final String PREFIX = "__DDefaultTableCellRenderer__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }
  }
}
