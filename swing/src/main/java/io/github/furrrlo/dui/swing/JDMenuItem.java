package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;
import io.github.furrrlo.dui.IdentifiableSupplier;

import javax.swing.*;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDMenuItem {
  public static DeclarativeComponent<JMenuItem> fn(IdentifiableConsumer<Decorator<JMenuItem>> body) {
    return fn(JMenuItem.class, JMenuItem::new, body);
  }

  public static DeclarativeComponent<JMenuItem> fn(Supplier<JMenuItem> factory,
      IdentifiableConsumer<Decorator<JMenuItem>> body) {
    return fn(JMenuItem.class, factory, body);
  }

  public static <T extends JMenuItem> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentifiableConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JMenuItem> extends DAbstractButton.Decorator<T> {
    private static final String PREFIX = "__JDMenuItem__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void accelerator(IdentifiableSupplier<? extends KeyStroke> accelerator) {
      attribute(PREFIX + "accelerator", JMenuItem::getAccelerator, JMenuItem::setAccelerator, accelerator);
    }

    public void armed(IdentifiableSupplier<Boolean> armed) {
      attribute(PREFIX + "armed", JMenuItem::isArmed, JMenuItem::setArmed, armed);
    }
  }
}
