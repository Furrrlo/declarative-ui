package io.github.furrrlo.dui.swing.plaf.basic;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;
import io.github.furrrlo.dui.swing.JDLabel;
import java.lang.Class;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.function.Supplier;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

@SuppressWarnings("unused")
public class DBasicComboBoxRenderer {
  public DeclarativeComponent<BasicComboBoxRenderer> fn(
      IdentifiableConsumer<Decorator<BasicComboBoxRenderer>> body) {
    return fn(BasicComboBoxRenderer.class, BasicComboBoxRenderer::new, body);
  }

  public DeclarativeComponent<BasicComboBoxRenderer> fn(Supplier<BasicComboBoxRenderer> factory,
      IdentifiableConsumer<Decorator<BasicComboBoxRenderer>> body) {
    return fn(BasicComboBoxRenderer.class, factory, body);
  }

  public <T extends BasicComboBoxRenderer> DeclarativeComponent<T> fn(Class<T> type,
      Supplier<T> factory, IdentifiableConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends BasicComboBoxRenderer> extends JDLabel.Decorator<T> {
    private static final String PREFIX = "__DBasicComboBoxRenderer__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }
  }
}
