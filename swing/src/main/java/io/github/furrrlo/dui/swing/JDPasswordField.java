package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentityFreeConsumer;
import io.github.furrrlo.dui.IdentityFreeSupplier;

import javax.swing.*;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDPasswordField {
  public static DeclarativeComponent<JPasswordField> fn(
      IdentityFreeConsumer<Decorator<JPasswordField>> body) {
    return fn(JPasswordField.class, JPasswordField::new, body);
  }

  public static DeclarativeComponent<JPasswordField> fn(Supplier<JPasswordField> factory,
      IdentityFreeConsumer<Decorator<JPasswordField>> body) {
    return fn(JPasswordField.class, factory, body);
  }

  public static <T extends JPasswordField> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentityFreeConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JPasswordField> extends JDTextField.Decorator<T> {
    private static final String PREFIX = "__JDPasswordField__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void echoChar(IdentityFreeSupplier<Character> echoChar) {
      attribute(PREFIX + "echoChar", JPasswordField::getEchoChar, JPasswordField::setEchoChar, echoChar);
    }
  }
}
