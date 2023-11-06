package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.DeclarativeComponentSupplier;
import io.github.furrrlo.dui.IdentifiableConsumer;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.lang.Class;
import java.lang.Integer;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.function.Supplier;
import javax.swing.JViewport;
import javax.swing.border.Border;
import javax.swing.plaf.ViewportUI;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class JDViewport {
  public static DeclarativeComponent<JViewport> fn(IdentifiableConsumer<Decorator<JViewport>> body) {
    return fn(JViewport.class, JViewport::new, body);
  }

  public static DeclarativeComponent<JViewport> fn(Supplier<JViewport> factory,
      IdentifiableConsumer<Decorator<JViewport>> body) {
    return fn(JViewport.class, factory, body);
  }

  public static <T extends JViewport> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentifiableConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JViewport> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__JDViewport__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void ui(Supplier<? extends ViewportUI> ui) {
      attribute(PREFIX + "ui", JViewport::getUI, JViewport::setUI, ui);
    }

    public void border(Supplier<? extends Border> border) {
      attribute(PREFIX + "border", JViewport::setBorder, border);
    }

    public void extentSize(Supplier<? extends Dimension> extentSize) {
      attribute(PREFIX + "extentSize", JViewport::getExtentSize, JViewport::setExtentSize, extentSize);
    }

    public void scrollMode(Supplier<Integer> scrollMode) {
      attribute(PREFIX + "scrollMode", JViewport::getScrollMode, JViewport::setScrollMode, scrollMode);
    }

    public void view(@Nullable DeclarativeComponentSupplier<? extends Component> view) {
      fnAttribute(PREFIX + "view", JViewport::getView, JViewport::setView, view);
    }

    public void viewPosition(Supplier<? extends Point> viewPosition) {
      attribute(PREFIX + "viewPosition", JViewport::getViewPosition, JViewport::setViewPosition, viewPosition);
    }

    public void viewSize(Supplier<? extends Dimension> viewSize) {
      attribute(PREFIX + "viewSize", JViewport::getViewSize, JViewport::setViewSize, viewSize);
    }
  }
}
