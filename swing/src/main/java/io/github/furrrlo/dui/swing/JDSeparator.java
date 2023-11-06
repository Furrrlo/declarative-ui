package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;
import java.lang.Class;
import java.lang.Integer;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.function.Supplier;
import javax.swing.JSeparator;
import javax.swing.plaf.SeparatorUI;

@SuppressWarnings("unused")
public class JDSeparator {
  public static DeclarativeComponent<JSeparator> fn(IdentifiableConsumer<Decorator<JSeparator>> body) {
    return fn(JSeparator.class, JSeparator::new, body);
  }

  public static DeclarativeComponent<JSeparator> fn(Supplier<JSeparator> factory,
      IdentifiableConsumer<Decorator<JSeparator>> body) {
    return fn(JSeparator.class, factory, body);
  }

  public static <T extends JSeparator> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentifiableConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JSeparator> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__JDSeparator__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void ui(Supplier<? extends SeparatorUI> ui) {
      attribute(PREFIX + "ui", JSeparator::getUI, JSeparator::setUI, ui);
    }

    public void orientation(Supplier<Integer> orientation) {
      attribute(PREFIX + "orientation", JSeparator::getOrientation, JSeparator::setOrientation, orientation);
    }
  }
}
