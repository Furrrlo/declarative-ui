package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentityFreeConsumer;
import io.github.furrrlo.dui.IdentityFreeSupplier;

import javax.swing.*;
import javax.swing.plaf.SliderUI;
import java.util.Dictionary;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDSlider {
  public static DeclarativeComponent<JSlider> fn(IdentityFreeConsumer<Decorator<JSlider>> body) {
    return fn(JSlider.class, JSlider::new, body);
  }

  public static DeclarativeComponent<JSlider> fn(Supplier<JSlider> factory,
      IdentityFreeConsumer<Decorator<JSlider>> body) {
    return fn(JSlider.class, factory, body);
  }

  public static <T extends JSlider> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentityFreeConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JSlider> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__JDSlider__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void ui(IdentityFreeSupplier<? extends SliderUI> ui) {
      attribute(PREFIX + "ui", JSlider::getUI, JSlider::setUI, ui);
    }

    public void extent(IdentityFreeSupplier<Integer> extent) {
      attribute(PREFIX + "extent", JSlider::getExtent, JSlider::setExtent, extent);
    }

    public void inverted(IdentityFreeSupplier<Boolean> inverted) {
      attribute(PREFIX + "inverted", JSlider::getInverted, JSlider::setInverted, inverted);
    }

    public void labelTable(IdentityFreeSupplier<? extends Dictionary<Integer, JComponent>> labelTable) {
      attribute(PREFIX + "labelTable", JSlider::getLabelTable, JSlider::setLabelTable, labelTable);
    }

    public void majorTickSpacing(IdentityFreeSupplier<Integer> majorTickSpacing) {
      attribute(PREFIX + "majorTickSpacing", JSlider::getMajorTickSpacing, JSlider::setMajorTickSpacing, majorTickSpacing);
    }

    public void maximum(IdentityFreeSupplier<Integer> maximum) {
      attribute(PREFIX + "maximum", JSlider::getMaximum, JSlider::setMaximum, maximum);
    }

    public void minimum(IdentityFreeSupplier<Integer> minimum) {
      attribute(PREFIX + "minimum", JSlider::getMinimum, JSlider::setMinimum, minimum);
    }

    public void minorTickSpacing(IdentityFreeSupplier<Integer> minorTickSpacing) {
      attribute(PREFIX + "minorTickSpacing", JSlider::getMinorTickSpacing, JSlider::setMinorTickSpacing, minorTickSpacing);
    }

    public void model(IdentityFreeSupplier<? extends BoundedRangeModel> model) {
      attribute(PREFIX + "model", JSlider::getModel, JSlider::setModel, model);
    }

    public void orientation(IdentityFreeSupplier<Integer> orientation) {
      attribute(PREFIX + "orientation", JSlider::getOrientation, JSlider::setOrientation, orientation);
    }

    public void paintLabels(IdentityFreeSupplier<Boolean> paintLabels) {
      attribute(PREFIX + "paintLabels", JSlider::getPaintLabels, JSlider::setPaintLabels, paintLabels);
    }

    public void paintTicks(IdentityFreeSupplier<Boolean> paintTicks) {
      attribute(PREFIX + "paintTicks", JSlider::getPaintTicks, JSlider::setPaintTicks, paintTicks);
    }

    public void paintTrack(IdentityFreeSupplier<Boolean> paintTrack) {
      attribute(PREFIX + "paintTrack", JSlider::getPaintTrack, JSlider::setPaintTrack, paintTrack);
    }

    public void snapToTicks(IdentityFreeSupplier<Boolean> snapToTicks) {
      attribute(PREFIX + "snapToTicks", JSlider::getSnapToTicks, JSlider::setSnapToTicks, snapToTicks);
    }

    public void value(IdentityFreeSupplier<Integer> value) {
      attribute(PREFIX + "value", JSlider::getValue, JSlider::setValue, value);
    }

    public void valueIsAdjusting(IdentityFreeSupplier<Boolean> valueIsAdjusting) {
      attribute(PREFIX + "valueIsAdjusting", JSlider::getValueIsAdjusting, JSlider::setValueIsAdjusting, valueIsAdjusting);
    }
  }
}
