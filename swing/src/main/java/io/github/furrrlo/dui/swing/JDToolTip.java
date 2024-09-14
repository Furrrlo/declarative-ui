package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.*;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDToolTip {
  public static DeclarativeComponent<JToolTip> fn(IdentityFreeConsumer<Decorator<JToolTip>> body) {
    return fn(JToolTip.class, JToolTip::new, body);
  }

  public static DeclarativeComponent<JToolTip> fn(Supplier<JToolTip> factory,
      IdentityFreeConsumer<Decorator<JToolTip>> body) {
    return fn(JToolTip.class, factory, body);
  }

  public static <T extends JToolTip> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentityFreeConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JToolTip> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__JDToolTip__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void component(@Nullable DeclarativeComponentSupplier<? extends JComponent> component) {
      fnAttribute(PREFIX + "component", JToolTip::getComponent, JToolTip::setComponent, component);
    }

    public void tipText(IdentityFreeSupplier<String> tipText) {
      attribute(PREFIX + "tipText", JToolTip::getTipText, JToolTip::setTipText, tipText);
    }
  }
}
