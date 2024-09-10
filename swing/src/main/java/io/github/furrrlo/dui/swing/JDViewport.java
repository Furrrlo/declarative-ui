package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.*;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.plaf.ViewportUI;
import java.awt.*;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDViewport {
  public static DeclarativeComponent<JViewport> fn(IdentityFreeConsumer<Decorator<JViewport>> body) {
    return fn(JViewport.class, JViewport::new, body);
  }

  public static DeclarativeComponent<JViewport> fn(Supplier<JViewport> factory,
      IdentityFreeConsumer<Decorator<JViewport>> body) {
    return fn(JViewport.class, factory, body);
  }

  public static <T extends JViewport> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentityFreeConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JViewport> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__JDViewport__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void ui(IdentityFreeSupplier<? extends ViewportUI> ui) {
      attribute(PREFIX + "ui", JViewport::getUI, JViewport::setUI, ui);
    }

    public void extentSize(IdentityFreeSupplier<? extends Dimension> extentSize) {
      attribute(PREFIX + "extentSize", JViewport::getExtentSize, JViewport::setExtentSize, extentSize);
    }

    public void scrollMode(IdentityFreeSupplier<Integer> scrollMode) {
      attribute(PREFIX + "scrollMode", JViewport::getScrollMode, JViewport::setScrollMode, scrollMode);
    }

    public void view(@Nullable DeclarativeComponentSupplier<? extends Component> view) {
      fnAttribute(PREFIX + "view", JViewport::getView, JViewport::setView, view);
    }

    public void viewPosition(IdentityFreeSupplier<? extends Point> viewPosition) {
      attribute(PREFIX + "viewPosition", JViewport::getViewPosition, JViewport::setViewPosition, viewPosition);
    }

    public void viewSize(IdentityFreeSupplier<? extends Dimension> viewSize) {
      attribute(PREFIX + "viewSize", JViewport::getViewSize, JViewport::setViewSize, viewSize);
    }
  }
}
