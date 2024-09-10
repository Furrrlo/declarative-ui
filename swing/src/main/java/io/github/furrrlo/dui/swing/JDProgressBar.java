package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentityFreeConsumer;
import io.github.furrrlo.dui.IdentityFreeSupplier;

import javax.swing.*;
import javax.swing.plaf.ProgressBarUI;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDProgressBar {
  public static DeclarativeComponent<JProgressBar> fn(IdentityFreeConsumer<Decorator<JProgressBar>> body) {
    return fn(JProgressBar.class, JProgressBar::new, body);
  }

  public static DeclarativeComponent<JProgressBar> fn(Supplier<JProgressBar> factory,
      IdentityFreeConsumer<Decorator<JProgressBar>> body) {
    return fn(JProgressBar.class, factory, body);
  }

  public static <T extends JProgressBar> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentityFreeConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JProgressBar> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__JDProgressBar__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void ui(IdentityFreeSupplier<? extends ProgressBarUI> ui) {
      attribute(PREFIX + "ui", JProgressBar::getUI, JProgressBar::setUI, ui);
    }

    public void borderPainted(IdentityFreeSupplier<Boolean> borderPainted) {
      attribute(PREFIX + "borderPainted", JProgressBar::isBorderPainted, JProgressBar::setBorderPainted, borderPainted);
    }

    public void indeterminate(IdentityFreeSupplier<Boolean> indeterminate) {
      attribute(PREFIX + "indeterminate", JProgressBar::isIndeterminate, JProgressBar::setIndeterminate, indeterminate);
    }

    public void maximum(IdentityFreeSupplier<Integer> maximum) {
      attribute(PREFIX + "maximum", JProgressBar::getMaximum, JProgressBar::setMaximum, maximum);
    }

    public void minimum(IdentityFreeSupplier<Integer> minimum) {
      attribute(PREFIX + "minimum", JProgressBar::getMinimum, JProgressBar::setMinimum, minimum);
    }

    public void model(IdentityFreeSupplier<? extends BoundedRangeModel> model) {
      attribute(PREFIX + "model", JProgressBar::getModel, JProgressBar::setModel, model);
    }

    public void orientation(IdentityFreeSupplier<Integer> orientation) {
      attribute(PREFIX + "orientation", JProgressBar::getOrientation, JProgressBar::setOrientation, orientation);
    }

    public void string(IdentityFreeSupplier<String> string) {
      attribute(PREFIX + "string", JProgressBar::getString, JProgressBar::setString, string);
    }

    public void stringPainted(IdentityFreeSupplier<Boolean> stringPainted) {
      attribute(PREFIX + "stringPainted", JProgressBar::isStringPainted, JProgressBar::setStringPainted, stringPainted);
    }

    public void value(IdentityFreeSupplier<Integer> value) {
      attribute(PREFIX + "value", JProgressBar::getValue, JProgressBar::setValue, value);
    }
  }
}
