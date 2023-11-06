package io.github.furrrlo.dui.swing.plaf.basic;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;
import io.github.furrrlo.dui.swing.JDButton;
import java.lang.Class;
import java.lang.Integer;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.function.Supplier;
import javax.swing.plaf.basic.BasicArrowButton;

@SuppressWarnings("unused")
public class DBasicArrowButton {
  public DeclarativeComponent<BasicArrowButton> fn(
      IdentifiableConsumer<Decorator<BasicArrowButton>> body) {
    return fn(BasicArrowButton.class, BasicArrowButton::new, body);
  }

  public DeclarativeComponent<BasicArrowButton> fn(Supplier<BasicArrowButton> factory,
      IdentifiableConsumer<Decorator<BasicArrowButton>> body) {
    return fn(BasicArrowButton.class, factory, body);
  }

  public <T extends BasicArrowButton> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentifiableConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends BasicArrowButton> extends JDButton.Decorator<T> {
    private static final String PREFIX = "__DBasicArrowButton__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void direction(Supplier<Integer> direction) {
      attribute(PREFIX + "direction", BasicArrowButton::getDirection, BasicArrowButton::setDirection, direction);
    }
  }
}
