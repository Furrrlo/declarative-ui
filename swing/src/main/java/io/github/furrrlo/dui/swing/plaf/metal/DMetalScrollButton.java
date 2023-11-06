package io.github.furrrlo.dui.swing.plaf.metal;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;
import io.github.furrrlo.dui.swing.DBasicArrowButton;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.function.Supplier;
import javax.swing.plaf.metal.MetalScrollButton;

@SuppressWarnings("unused")
public class DMetalScrollButton {
  public DeclarativeComponent<MetalScrollButton> fn(
      IdentifiableConsumer<Decorator<MetalScrollButton>> body) {
    return fn(MetalScrollButton.class, MetalScrollButton::new, body);
  }

  public DeclarativeComponent<MetalScrollButton> fn(Supplier<MetalScrollButton> factory,
      IdentifiableConsumer<Decorator<MetalScrollButton>> body) {
    return fn(MetalScrollButton.class, factory, body);
  }

  public <T extends MetalScrollButton> DeclarativeComponent<T> fn(Class<T> type,
      Supplier<T> factory, IdentifiableConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends MetalScrollButton> extends DBasicArrowButton.Decorator<T> {
    private static final String PREFIX = "__DMetalScrollButton__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void freeStanding(Supplier<Boolean> freeStanding) {
      attribute(PREFIX + "freeStanding", MetalScrollButton::setFreeStanding, freeStanding);
    }
  }
}
