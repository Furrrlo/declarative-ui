package io.github.furrrlo.dui.swing.plaf.basic;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;
import io.github.furrrlo.dui.swing.JDComponent;
import java.lang.Class;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.function.Supplier;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;

@SuppressWarnings("unused")
public class DBasicInternalFrameTitlePane {
  public DeclarativeComponent<BasicInternalFrameTitlePane> fn(
      IdentifiableConsumer<Decorator<BasicInternalFrameTitlePane>> body) {
    return fn(BasicInternalFrameTitlePane.class, BasicInternalFrameTitlePane::new, body);
  }

  public DeclarativeComponent<BasicInternalFrameTitlePane> fn(
      Supplier<BasicInternalFrameTitlePane> factory,
      IdentifiableConsumer<Decorator<BasicInternalFrameTitlePane>> body) {
    return fn(BasicInternalFrameTitlePane.class, factory, body);
  }

  public <T extends BasicInternalFrameTitlePane> DeclarativeComponent<T> fn(Class<T> type,
      Supplier<T> factory, IdentifiableConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends BasicInternalFrameTitlePane> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__DBasicInternalFrameTitlePane__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }
  }
}
