package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;
import io.github.furrrlo.dui.IdentifiableSupplier;

import javax.swing.*;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDFormattedTextField {
  public static DeclarativeComponent<JFormattedTextField> fn(
      IdentifiableConsumer<Decorator<JFormattedTextField>> body) {
    return fn(JFormattedTextField.class, JFormattedTextField::new, body);
  }

  public static DeclarativeComponent<JFormattedTextField> fn(Supplier<JFormattedTextField> factory,
      IdentifiableConsumer<Decorator<JFormattedTextField>> body) {
    return fn(JFormattedTextField.class, factory, body);
  }

  public static <T extends JFormattedTextField> DeclarativeComponent<T> fn(Class<T> type,
      Supplier<T> factory, IdentifiableConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JFormattedTextField> extends JDTextField.Decorator<T> {
    private static final String PREFIX = "__JDFormattedTextField__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void focusLostBehavior(IdentifiableSupplier<Integer> focusLostBehavior) {
      attribute(PREFIX + "focusLostBehavior", JFormattedTextField::getFocusLostBehavior, JFormattedTextField::setFocusLostBehavior, focusLostBehavior);
    }

    public void formatterFactory(
        IdentifiableSupplier<? extends JFormattedTextField.AbstractFormatterFactory> formatterFactory) {
      attribute(PREFIX + "formatterFactory", JFormattedTextField::getFormatterFactory, JFormattedTextField::setFormatterFactory, formatterFactory);
    }

    public void value(IdentifiableSupplier<?> value) {
      attribute(PREFIX + "value", JFormattedTextField::getValue, JFormattedTextField::setValue, value);
    }
  }
}
