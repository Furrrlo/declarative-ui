package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentityFreeConsumer;
import io.github.furrrlo.dui.IdentityFreeSupplier;

import javax.swing.*;
import javax.swing.plaf.ToolBarUI;
import java.awt.*;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDToolBar {
  public static DeclarativeComponent<JToolBar> fn(IdentityFreeConsumer<Decorator<JToolBar>> body) {
    return fn(JToolBar.class, JToolBar::new, body);
  }

  public static DeclarativeComponent<JToolBar> fn(Supplier<JToolBar> factory,
      IdentityFreeConsumer<Decorator<JToolBar>> body) {
    return fn(JToolBar.class, factory, body);
  }

  public static <T extends JToolBar> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentityFreeConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JToolBar> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__JDToolBar__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void ui(IdentityFreeSupplier<? extends ToolBarUI> ui) {
      attribute(PREFIX + "ui", JToolBar::getUI, JToolBar::setUI, ui);
    }

    public void borderPainted(IdentityFreeSupplier<Boolean> borderPainted) {
      attribute(PREFIX + "borderPainted", JToolBar::isBorderPainted, JToolBar::setBorderPainted, borderPainted);
    }

    public void floatable(IdentityFreeSupplier<Boolean> floatable) {
      attribute(PREFIX + "floatable", JToolBar::isFloatable, JToolBar::setFloatable, floatable);
    }

    public void margin(IdentityFreeSupplier<? extends Insets> margin) {
      attribute(PREFIX + "margin", JToolBar::getMargin, JToolBar::setMargin, margin);
    }

    public void orientation(IdentityFreeSupplier<Integer> orientation) {
      attribute(PREFIX + "orientation", JToolBar::getOrientation, JToolBar::setOrientation, orientation);
    }

    public void rollover(IdentityFreeSupplier<Boolean> rollover) {
      attribute(PREFIX + "rollover", JToolBar::isRollover, JToolBar::setRollover, rollover);
    }
  }
}
