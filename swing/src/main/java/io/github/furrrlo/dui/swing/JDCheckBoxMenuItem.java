package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentityFreeConsumer;
import io.github.furrrlo.dui.IdentityFreeSupplier;

import javax.swing.*;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDCheckBoxMenuItem {
  public static DeclarativeComponent<JCheckBoxMenuItem> fn(
      IdentityFreeConsumer<Decorator<JCheckBoxMenuItem>> body) {
    return fn(JCheckBoxMenuItem.class, JCheckBoxMenuItem::new, body);
  }

  public static DeclarativeComponent<JCheckBoxMenuItem> fn(Supplier<JCheckBoxMenuItem> factory,
      IdentityFreeConsumer<Decorator<JCheckBoxMenuItem>> body) {
    return fn(JCheckBoxMenuItem.class, factory, body);
  }

  public static <T extends JCheckBoxMenuItem> DeclarativeComponent<T> fn(Class<T> type,
      Supplier<T> factory, IdentityFreeConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JCheckBoxMenuItem> extends JDMenuItem.Decorator<T> {
    private static final String PREFIX = "__JDCheckBoxMenuItem__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void state(IdentityFreeSupplier<Boolean> state) {
      attribute(PREFIX + "state", JCheckBoxMenuItem::getState, JCheckBoxMenuItem::setState, state);
    }
  }
}
