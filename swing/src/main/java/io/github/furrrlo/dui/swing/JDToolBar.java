package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.Integer;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.function.Supplier;
import javax.swing.JToolBar;
import javax.swing.plaf.ToolBarUI;

@SuppressWarnings("unused")
public class JDToolBar {
  public static DeclarativeComponent<JToolBar> fn(IdentifiableConsumer<Decorator<JToolBar>> body) {
    return fn(JToolBar.class, JToolBar::new, body);
  }

  public static DeclarativeComponent<JToolBar> fn(Supplier<JToolBar> factory,
      IdentifiableConsumer<Decorator<JToolBar>> body) {
    return fn(JToolBar.class, factory, body);
  }

  public static <T extends JToolBar> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentifiableConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JToolBar> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__JDToolBar__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void ui(Supplier<? extends ToolBarUI> ui) {
      attribute(PREFIX + "ui", JToolBar::getUI, JToolBar::setUI, ui);
    }

    public void borderPainted(Supplier<Boolean> borderPainted) {
      attribute(PREFIX + "borderPainted", JToolBar::isBorderPainted, JToolBar::setBorderPainted, borderPainted);
    }

    public void floatable(Supplier<Boolean> floatable) {
      attribute(PREFIX + "floatable", JToolBar::isFloatable, JToolBar::setFloatable, floatable);
    }

    public void layout(Supplier<? extends LayoutManager> layout) {
      attribute(PREFIX + "layout", JToolBar::setLayout, layout);
    }

    public void margin(Supplier<? extends Insets> margin) {
      attribute(PREFIX + "margin", JToolBar::getMargin, JToolBar::setMargin, margin);
    }

    public void orientation(Supplier<Integer> orientation) {
      attribute(PREFIX + "orientation", JToolBar::getOrientation, JToolBar::setOrientation, orientation);
    }

    public void rollover(Supplier<Boolean> rollover) {
      attribute(PREFIX + "rollover", JToolBar::isRollover, JToolBar::setRollover, rollover);
    }
  }
}
