package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;
import java.lang.Character;
import java.lang.Class;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.function.Supplier;
import javax.swing.JPasswordField;

@SuppressWarnings("unused")
public class JDPasswordField {
  public static DeclarativeComponent<JPasswordField> fn(
      IdentifiableConsumer<Decorator<JPasswordField>> body) {
    return fn(JPasswordField.class, JPasswordField::new, body);
  }

  public static DeclarativeComponent<JPasswordField> fn(Supplier<JPasswordField> factory,
      IdentifiableConsumer<Decorator<JPasswordField>> body) {
    return fn(JPasswordField.class, factory, body);
  }

  public static <T extends JPasswordField> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentifiableConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JPasswordField> extends JDTextField.Decorator<T> {
    private static final String PREFIX = "__JDPasswordField__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void echoChar(Supplier<Character> echoChar) {
      attribute(PREFIX + "echoChar", JPasswordField::getEchoChar, JPasswordField::setEchoChar, echoChar);
    }

    public void text(Supplier<String> text) {
      attribute(PREFIX + "text", JPasswordField::getText, JPasswordField::setText, text);
    }
  }
}
