package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentityFreeConsumer;
import java.lang.Class;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.function.Supplier;
import javax.swing.JRadioButtonMenuItem;

@SuppressWarnings("unused")
public class JDRadioButtonMenuItem {
  public static DeclarativeComponent<JRadioButtonMenuItem> fn(
      IdentityFreeConsumer<Decorator<JRadioButtonMenuItem>> body) {
    return fn(JRadioButtonMenuItem.class, JRadioButtonMenuItem::new, body);
  }

  public static DeclarativeComponent<JRadioButtonMenuItem> fn(Supplier<JRadioButtonMenuItem> factory,
      IdentityFreeConsumer<Decorator<JRadioButtonMenuItem>> body) {
    return fn(JRadioButtonMenuItem.class, factory, body);
  }

  public static <T extends JRadioButtonMenuItem> DeclarativeComponent<T> fn(Class<T> type,
      Supplier<T> factory, IdentityFreeConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JRadioButtonMenuItem> extends JDMenuItem.Decorator<T> {
    private static final String PREFIX = "__JDRadioButtonMenuItem__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }
  }
}
