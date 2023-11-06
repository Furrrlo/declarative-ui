package io.github.furrrlo.dui.swing.plaf.metal;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;
import io.github.furrrlo.dui.swing.DBasicInternalFrameTitlePane;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.function.Supplier;
import javax.swing.plaf.metal.MetalInternalFrameTitlePane;

@SuppressWarnings("unused")
public class DMetalInternalFrameTitlePane {
  public DeclarativeComponent<MetalInternalFrameTitlePane> fn(
      IdentifiableConsumer<Decorator<MetalInternalFrameTitlePane>> body) {
    return fn(MetalInternalFrameTitlePane.class, MetalInternalFrameTitlePane::new, body);
  }

  public DeclarativeComponent<MetalInternalFrameTitlePane> fn(
      Supplier<MetalInternalFrameTitlePane> factory,
      IdentifiableConsumer<Decorator<MetalInternalFrameTitlePane>> body) {
    return fn(MetalInternalFrameTitlePane.class, factory, body);
  }

  public <T extends MetalInternalFrameTitlePane> DeclarativeComponent<T> fn(Class<T> type,
      Supplier<T> factory, IdentifiableConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends MetalInternalFrameTitlePane> extends DBasicInternalFrameTitlePane.Decorator<T> {
    private static final String PREFIX = "__DMetalInternalFrameTitlePane__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void palette(Supplier<Boolean> palette) {
      attribute(PREFIX + "palette", MetalInternalFrameTitlePane::setPalette, palette);
    }
  }
}
