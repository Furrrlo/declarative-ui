package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;

import javax.swing.*;
import javax.swing.plaf.ScrollBarUI;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDScrollBar {
  public static DeclarativeComponent<JScrollBar> fn(IdentifiableConsumer<Decorator<JScrollBar>> body) {
    return fn(JScrollBar.class, JScrollBar::new, body);
  }

  public static DeclarativeComponent<JScrollBar> fn(Supplier<JScrollBar> factory,
      IdentifiableConsumer<Decorator<JScrollBar>> body) {
    return fn(JScrollBar.class, factory, body);
  }

  public static <T extends JScrollBar> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentifiableConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JScrollBar> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__JDScrollBar__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void ui(Supplier<? extends ScrollBarUI> ui) {
      attribute(PREFIX + "ui", JScrollBar::getUI, JScrollBar::setUI, ui);
    }

    public void blockIncrement(Supplier<Integer> blockIncrement) {
      attribute(PREFIX + "blockIncrement", JScrollBar::getBlockIncrement, JScrollBar::setBlockIncrement, blockIncrement);
    }

    public void maximum(Supplier<Integer> maximum) {
      attribute(PREFIX + "maximum", JScrollBar::getMaximum, JScrollBar::setMaximum, maximum);
    }

    public void minimum(Supplier<Integer> minimum) {
      attribute(PREFIX + "minimum", JScrollBar::getMinimum, JScrollBar::setMinimum, minimum);
    }

    public void model(Supplier<? extends BoundedRangeModel> model) {
      attribute(PREFIX + "model", JScrollBar::getModel, JScrollBar::setModel, model);
    }

    public void orientation(Supplier<Integer> orientation) {
      attribute(PREFIX + "orientation", JScrollBar::getOrientation, JScrollBar::setOrientation, orientation);
    }

    public void unitIncrement(Supplier<Integer> unitIncrement) {
      attribute(PREFIX + "unitIncrement", JScrollBar::getUnitIncrement, JScrollBar::setUnitIncrement, unitIncrement);
    }

    public void value(Supplier<Integer> value) {
      attribute(PREFIX + "value", JScrollBar::getValue, JScrollBar::setValue, value);
    }

    public void valueIsAdjusting(Supplier<Boolean> valueIsAdjusting) {
      attribute(PREFIX + "valueIsAdjusting", JScrollBar::getValueIsAdjusting, JScrollBar::setValueIsAdjusting, valueIsAdjusting);
    }

    public void visibleAmount(Supplier<Integer> visibleAmount) {
      attribute(PREFIX + "visibleAmount", JScrollBar::getVisibleAmount, JScrollBar::setVisibleAmount, visibleAmount);
    }
  }
}
