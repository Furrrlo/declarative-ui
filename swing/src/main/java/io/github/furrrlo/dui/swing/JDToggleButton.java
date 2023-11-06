package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;
import java.lang.Class;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.function.Supplier;
import javax.swing.JToggleButton;

@SuppressWarnings("unused")
public class JDToggleButton {
  public static DeclarativeComponent<JToggleButton> fn(
      IdentifiableConsumer<Decorator<JToggleButton>> body) {
    return fn(JToggleButton.class, JToggleButton::new, body);
  }

  public static DeclarativeComponent<JToggleButton> fn(Supplier<JToggleButton> factory,
      IdentifiableConsumer<Decorator<JToggleButton>> body) {
    return fn(JToggleButton.class, factory, body);
  }

  public static <T extends JToggleButton> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentifiableConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JToggleButton> extends DAbstractButton.Decorator<T> {
    private static final String PREFIX = "__JDToggleButton__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }
  }
}
