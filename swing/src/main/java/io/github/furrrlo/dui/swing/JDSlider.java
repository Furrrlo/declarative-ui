package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;
import io.github.furrrlo.dui.IdentifiableSupplier;

import javax.swing.*;
import javax.swing.plaf.SliderUI;
import java.util.Dictionary;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDSlider {
  public static DeclarativeComponent<JSlider> fn(IdentifiableConsumer<Decorator<JSlider>> body) {
    return fn(JSlider.class, JSlider::new, body);
  }

  public static DeclarativeComponent<JSlider> fn(Supplier<JSlider> factory,
      IdentifiableConsumer<Decorator<JSlider>> body) {
    return fn(JSlider.class, factory, body);
  }

  public static <T extends JSlider> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentifiableConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JSlider> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__JDSlider__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void ui(IdentifiableSupplier<? extends SliderUI> ui) {
      attribute(PREFIX + "ui", JSlider::getUI, JSlider::setUI, ui);
    }

    public void extent(IdentifiableSupplier<Integer> extent) {
      attribute(PREFIX + "extent", JSlider::getExtent, JSlider::setExtent, extent);
    }

    public void inverted(IdentifiableSupplier<Boolean> inverted) {
      attribute(PREFIX + "inverted", JSlider::getInverted, JSlider::setInverted, inverted);
    }

    public void labelTable(IdentifiableSupplier<? extends Dictionary<Integer, JComponent>> labelTable) {
      attribute(PREFIX + "labelTable", JSlider::getLabelTable, JSlider::setLabelTable, labelTable);
    }

    public void majorTickSpacing(IdentifiableSupplier<Integer> majorTickSpacing) {
      attribute(PREFIX + "majorTickSpacing", JSlider::getMajorTickSpacing, JSlider::setMajorTickSpacing, majorTickSpacing);
    }

    public void maximum(IdentifiableSupplier<Integer> maximum) {
      attribute(PREFIX + "maximum", JSlider::getMaximum, JSlider::setMaximum, maximum);
    }

    public void minimum(IdentifiableSupplier<Integer> minimum) {
      attribute(PREFIX + "minimum", JSlider::getMinimum, JSlider::setMinimum, minimum);
    }

    public void minorTickSpacing(IdentifiableSupplier<Integer> minorTickSpacing) {
      attribute(PREFIX + "minorTickSpacing", JSlider::getMinorTickSpacing, JSlider::setMinorTickSpacing, minorTickSpacing);
    }

    public void model(IdentifiableSupplier<? extends BoundedRangeModel> model) {
      attribute(PREFIX + "model", JSlider::getModel, JSlider::setModel, model);
    }

    public void orientation(IdentifiableSupplier<Integer> orientation) {
      attribute(PREFIX + "orientation", JSlider::getOrientation, JSlider::setOrientation, orientation);
    }

    public void paintLabels(IdentifiableSupplier<Boolean> paintLabels) {
      attribute(PREFIX + "paintLabels", JSlider::getPaintLabels, JSlider::setPaintLabels, paintLabels);
    }

    public void paintTicks(IdentifiableSupplier<Boolean> paintTicks) {
      attribute(PREFIX + "paintTicks", JSlider::getPaintTicks, JSlider::setPaintTicks, paintTicks);
    }

    public void paintTrack(IdentifiableSupplier<Boolean> paintTrack) {
      attribute(PREFIX + "paintTrack", JSlider::getPaintTrack, JSlider::setPaintTrack, paintTrack);
    }

    public void snapToTicks(IdentifiableSupplier<Boolean> snapToTicks) {
      attribute(PREFIX + "snapToTicks", JSlider::getSnapToTicks, JSlider::setSnapToTicks, snapToTicks);
    }

    public void value(IdentifiableSupplier<Integer> value) {
      attribute(PREFIX + "value", JSlider::getValue, JSlider::setValue, value);
    }

    public void valueIsAdjusting(IdentifiableSupplier<Boolean> valueIsAdjusting) {
      attribute(PREFIX + "valueIsAdjusting", JSlider::getValueIsAdjusting, JSlider::setValueIsAdjusting, valueIsAdjusting);
    }
  }
}
