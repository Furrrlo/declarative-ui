package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.DeclarativeComponentSupplier;
import io.github.furrrlo.dui.IdentifiableConsumer;
import java.lang.Class;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.function.Supplier;
import javax.swing.JComponent;
import javax.swing.JToolTip;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class JDToolTip {
  public static DeclarativeComponent<JToolTip> fn(IdentifiableConsumer<Decorator<JToolTip>> body) {
    return fn(JToolTip.class, JToolTip::new, body);
  }

  public static DeclarativeComponent<JToolTip> fn(Supplier<JToolTip> factory,
      IdentifiableConsumer<Decorator<JToolTip>> body) {
    return fn(JToolTip.class, factory, body);
  }

  public static <T extends JToolTip> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentifiableConsumer<Decorator<T>> body) {
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

    public void tipText(Supplier<String> tipText) {
      attribute(PREFIX + "tipText", JToolTip::getTipText, JToolTip::setTipText, tipText);
    }
  }
}
