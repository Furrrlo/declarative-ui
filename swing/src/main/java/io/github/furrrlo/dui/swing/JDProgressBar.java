package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.Integer;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.function.Supplier;
import javax.swing.BoundedRangeModel;
import javax.swing.JProgressBar;
import javax.swing.plaf.ProgressBarUI;

@SuppressWarnings("unused")
public class JDProgressBar {
  public static DeclarativeComponent<JProgressBar> fn(IdentifiableConsumer<Decorator<JProgressBar>> body) {
    return fn(JProgressBar.class, JProgressBar::new, body);
  }

  public static DeclarativeComponent<JProgressBar> fn(Supplier<JProgressBar> factory,
      IdentifiableConsumer<Decorator<JProgressBar>> body) {
    return fn(JProgressBar.class, factory, body);
  }

  public static <T extends JProgressBar> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentifiableConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JProgressBar> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__JDProgressBar__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void ui(Supplier<? extends ProgressBarUI> ui) {
      attribute(PREFIX + "ui", JProgressBar::getUI, JProgressBar::setUI, ui);
    }

    public void borderPainted(Supplier<Boolean> borderPainted) {
      attribute(PREFIX + "borderPainted", JProgressBar::isBorderPainted, JProgressBar::setBorderPainted, borderPainted);
    }

    public void indeterminate(Supplier<Boolean> indeterminate) {
      attribute(PREFIX + "indeterminate", JProgressBar::isIndeterminate, JProgressBar::setIndeterminate, indeterminate);
    }

    public void maximum(Supplier<Integer> maximum) {
      attribute(PREFIX + "maximum", JProgressBar::getMaximum, JProgressBar::setMaximum, maximum);
    }

    public void minimum(Supplier<Integer> minimum) {
      attribute(PREFIX + "minimum", JProgressBar::getMinimum, JProgressBar::setMinimum, minimum);
    }

    public void model(Supplier<? extends BoundedRangeModel> model) {
      attribute(PREFIX + "model", JProgressBar::getModel, JProgressBar::setModel, model);
    }

    public void orientation(Supplier<Integer> orientation) {
      attribute(PREFIX + "orientation", JProgressBar::getOrientation, JProgressBar::setOrientation, orientation);
    }

    public void string(Supplier<String> string) {
      attribute(PREFIX + "string", JProgressBar::getString, JProgressBar::setString, string);
    }

    public void stringPainted(Supplier<Boolean> stringPainted) {
      attribute(PREFIX + "stringPainted", JProgressBar::isStringPainted, JProgressBar::setStringPainted, stringPainted);
    }

    public void value(Supplier<Integer> value) {
      attribute(PREFIX + "value", JProgressBar::getValue, JProgressBar::setValue, value);
    }
  }
}
