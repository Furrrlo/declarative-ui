package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.*;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.plaf.DesktopPaneUI;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDDesktopPane {
  public static DeclarativeComponent<JDesktopPane> fn(IdentifiableConsumer<Decorator<JDesktopPane>> body) {
    return fn(JDesktopPane.class, JDesktopPane::new, body);
  }

  public static DeclarativeComponent<JDesktopPane> fn(Supplier<JDesktopPane> factory,
      IdentifiableConsumer<Decorator<JDesktopPane>> body) {
    return fn(JDesktopPane.class, factory, body);
  }

  public static <T extends JDesktopPane> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentifiableConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JDesktopPane> extends JDLayeredPane.Decorator<T> {
    private static final String PREFIX = "__JDDesktopPane__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void ui(IdentifiableSupplier<? extends DesktopPaneUI> ui) {
      attribute(PREFIX + "ui", JDesktopPane::getUI, JDesktopPane::setUI, ui);
    }

    public void desktopManager(IdentifiableSupplier<? extends DesktopManager> desktopManager) {
      attribute(PREFIX + "desktopManager", JDesktopPane::getDesktopManager, JDesktopPane::setDesktopManager, desktopManager);
    }

    public void dragMode(IdentifiableSupplier<Integer> dragMode) {
      attribute(PREFIX + "dragMode", JDesktopPane::getDragMode, JDesktopPane::setDragMode, dragMode);
    }

    public void selectedFrame(
        @Nullable DeclarativeComponentSupplier<? extends JInternalFrame> selectedFrame) {
      fnAttribute(PREFIX + "selectedFrame", JDesktopPane::getSelectedFrame, JDesktopPane::setSelectedFrame, selectedFrame);
    }
  }
}
