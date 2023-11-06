package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.function.Supplier;
import javax.swing.ButtonModel;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.plaf.MenuItemUI;

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

    public void ui(Supplier<? extends MenuItemUI> ui) {
      attribute(PREFIX + "ui", JMenuItem::setUI, ui);
    }

    public void accelerator(Supplier<? extends KeyStroke> accelerator) {
      attribute(PREFIX + "accelerator", JMenuItem::getAccelerator, JMenuItem::setAccelerator, accelerator);
    }

    public void armed(Supplier<Boolean> armed) {
      attribute(PREFIX + "armed", JMenuItem::isArmed, JMenuItem::setArmed, armed);
    }

    public void enabled(Supplier<Boolean> enabled) {
      attribute(PREFIX + "enabled", JMenuItem::setEnabled, enabled);
    }

    public void model(Supplier<? extends ButtonModel> model) {
      attribute(PREFIX + "model", JMenuItem::setModel, model);
    }
  }
}
