package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.DeclarativeComponentSupplier;
import io.github.furrrlo.dui.IdentifiableConsumer;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDLayer {
  public static <V extends Component> DeclarativeComponent<JLayer<V>> fn(IdentifiableConsumer<Decorator<V, JLayer<V>>> body) {
    return fn(JLayer::new, body);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public static <V extends Component> DeclarativeComponent<JLayer<V>>  fn(Supplier<JLayer<V>> factory,
                                                                   IdentifiableConsumer<Decorator<V, JLayer<V>>> body) {
    return fn((Class<JLayer<V>>) (Class) JLayer.class, factory, body);
  }

  public static <V extends Component, T extends JLayer<V>> DeclarativeComponent<T> fn(Class<T> type,
                                                                               Supplier<T> factory,
                                                                               IdentifiableConsumer<Decorator<V, T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<V extends Component, T extends JLayer<V>> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__JDLayer__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void ui(Supplier<? extends LayerUI<? super V>> ui) {
      this.<LayerUI<? super V>>attribute(PREFIX + "ui", JLayer::getUI, JLayer::setUI, ui);
    }

    public void border(Supplier<? extends Border> border) {
      attribute(PREFIX + "border", JLayer::getBorder, JLayer::setBorder, border);
    }

    public void glassPane(@Nullable DeclarativeComponentSupplier<? extends JPanel> glassPane) {
      fnAttribute(PREFIX + "glassPane", JLayer::getGlassPane, JLayer::setGlassPane, glassPane);
    }

    public void layerEventMask(Supplier<Long> layerEventMask) {
      attribute(PREFIX + "layerEventMask", JLayer::getLayerEventMask, JLayer::setLayerEventMask, layerEventMask);
    }

    public void layout(Supplier<? extends LayoutManager> layout) {
      attribute(PREFIX + "layout", JLayer::setLayout, layout);
    }

    public void view(@Nullable DeclarativeComponentSupplier<? extends V> view) {
      fnAttribute(PREFIX + "view", JLayer::getView, JLayer::setView, view);
    }
  }
}
