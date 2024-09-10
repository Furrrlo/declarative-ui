package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.*;
import io.leangen.geantyref.TypeFactory;
import io.leangen.geantyref.TypeToken;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDLayer {

  public static <V extends Component> DeclarativeComponent<JLayer<V>> fn(Class<V> type,
                                                                         IdentityFreeConsumer<Decorator<V, JLayer<V>>> body) {
    return fn(TypeToken.get(type), body);
  }

  @SuppressWarnings("unchecked")
  public static <V extends Component> DeclarativeComponent<JLayer<V>> fn(TypeToken<V> type,
                                                                         IdentityFreeConsumer<Decorator<V, JLayer<V>>> body) {
    return fn(
            (TypeToken<JLayer<V>>) TypeToken.get(TypeFactory.parameterizedClass(JList.class, type.getType())),
            JLayer::new,
            body);
  }

  public static <V extends Component, T extends JLayer<V>> DeclarativeComponent<T> fn(TypeToken<T> type,
                                                                                      Supplier<T> factory,
                                                                                      IdentityFreeConsumer<Decorator<V, T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<V extends Component, T extends JLayer<V>> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__JDLayer__";

    protected Decorator(TypeToken<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void ui(IdentityFreeSupplier<? extends LayerUI<? super V>> ui) {
      this.<LayerUI<? super V>>attribute(PREFIX + "ui", JLayer::getUI, JLayer::setUI, ui);
    }

    public void glassPane(@Nullable DeclarativeComponentSupplier<? extends JPanel> glassPane) {
      fnAttribute(PREFIX + "glassPane", JLayer::getGlassPane, JLayer::setGlassPane, glassPane);
    }

    public void layerEventMask(IdentityFreeSupplier<Long> layerEventMask) {
      attribute(PREFIX + "layerEventMask", JLayer::getLayerEventMask, JLayer::setLayerEventMask, layerEventMask);
    }

    public void view(@Nullable DeclarativeComponentSupplier<? extends V> view) {
      fnAttribute(PREFIX + "view", JLayer::getView, JLayer::setView, view);
    }
  }
}
