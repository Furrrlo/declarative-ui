package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentityFreeConsumer;

import javax.swing.*;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class DBox {
  public static DeclarativeComponent<Box> fn(Supplier<Box> factory,
      IdentityFreeConsumer<Decorator<Box>> body) {
    return fn(Box.class, factory, body);
  }

  public static <T extends Box> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentityFreeConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends Box> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__DBox__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }
  }
}
