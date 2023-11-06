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
  public static DeclarativeComponent<JLayer> fn(IdentifiableConsumer<Decorator<JLayer>> body) {
    return fn(JLayer.class, JLayer::new, body);
  }

  public static DeclarativeComponent<JLayer> fn(Supplier<JLayer> factory,
      IdentifiableConsumer<Decorator<JLayer>> body) {
    return fn(JLayer.class, factory, body);
  }

  public static <T extends JLayer> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentifiableConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JLayer> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__JDLayer__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void ui(Supplier<? extends LayerUI> ui) {
      attribute(PREFIX + "ui", JLayer::getUI, JLayer::setUI, ui);
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

    public void view(@Nullable DeclarativeComponentSupplier<? extends Component> view) {
      fnAttribute(PREFIX + "view", JLayer::getView, JLayer::setView, view);
    }
  }
}
