package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.function.Supplier;
import javax.swing.JCheckBoxMenuItem;

@SuppressWarnings("unused")
public class JDCheckBoxMenuItem {
  public static DeclarativeComponent<JCheckBoxMenuItem> fn(
      IdentifiableConsumer<Decorator<JCheckBoxMenuItem>> body) {
    return fn(JCheckBoxMenuItem.class, JCheckBoxMenuItem::new, body);
  }

  public static DeclarativeComponent<JCheckBoxMenuItem> fn(Supplier<JCheckBoxMenuItem> factory,
      IdentifiableConsumer<Decorator<JCheckBoxMenuItem>> body) {
    return fn(JCheckBoxMenuItem.class, factory, body);
  }

  public static <T extends JCheckBoxMenuItem> DeclarativeComponent<T> fn(Class<T> type,
      Supplier<T> factory, IdentifiableConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JCheckBoxMenuItem> extends JDMenuItem.Decorator<T> {
    private static final String PREFIX = "__JDCheckBoxMenuItem__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void state(Supplier<Boolean> state) {
      attribute(PREFIX + "state", JCheckBoxMenuItem::getState, JCheckBoxMenuItem::setState, state);
    }
  }
}
