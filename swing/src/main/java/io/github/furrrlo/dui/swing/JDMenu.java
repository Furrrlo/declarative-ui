package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;
import java.awt.ComponentOrientation;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.Integer;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.function.Supplier;
import javax.swing.ButtonModel;
import javax.swing.JMenu;
import javax.swing.KeyStroke;

@SuppressWarnings("unused")
public class JDMenu {
  public static DeclarativeComponent<JMenu> fn(IdentifiableConsumer<Decorator<JMenu>> body) {
    return fn(JMenu.class, JMenu::new, body);
  }

  public static DeclarativeComponent<JMenu> fn(Supplier<JMenu> factory,
      IdentifiableConsumer<Decorator<JMenu>> body) {
    return fn(JMenu.class, factory, body);
  }

  public static <T extends JMenu> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentifiableConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JMenu> extends JDMenuItem.Decorator<T> {
    private static final String PREFIX = "__JDMenu__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void accelerator(Supplier<? extends KeyStroke> accelerator) {
      attribute(PREFIX + "accelerator", JMenu::setAccelerator, accelerator);
    }

    public void componentOrientation(Supplier<ComponentOrientation> componentOrientation) {
      attribute(PREFIX + "componentOrientation", JMenu::setComponentOrientation, componentOrientation);
    }

    public void delay(Supplier<Integer> delay) {
      attribute(PREFIX + "delay", JMenu::getDelay, JMenu::setDelay, delay);
    }

    public void model(Supplier<? extends ButtonModel> model) {
      attribute(PREFIX + "model", JMenu::setModel, model);
    }

    public void popupMenuVisible(Supplier<Boolean> popupMenuVisible) {
      attribute(PREFIX + "popupMenuVisible", JMenu::isPopupMenuVisible, JMenu::setPopupMenuVisible, popupMenuVisible);
    }

    public void selected(Supplier<Boolean> selected) {
      attribute(PREFIX + "selected", JMenu::isSelected, JMenu::setSelected, selected);
    }
  }
}
