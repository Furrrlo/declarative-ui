package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.DeclarativeComponentSupplier;
import io.github.furrrlo.dui.IdentifiableConsumer;
import java.lang.Class;
import java.lang.Integer;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.function.Supplier;
import javax.swing.DesktopManager;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.plaf.DesktopPaneUI;
import org.jetbrains.annotations.Nullable;

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

    public void ui(Supplier<? extends DesktopPaneUI> ui) {
      attribute(PREFIX + "ui", JDesktopPane::getUI, JDesktopPane::setUI, ui);
    }

    public void desktopManager(Supplier<? extends DesktopManager> desktopManager) {
      attribute(PREFIX + "desktopManager", JDesktopPane::getDesktopManager, JDesktopPane::setDesktopManager, desktopManager);
    }

    public void dragMode(Supplier<Integer> dragMode) {
      attribute(PREFIX + "dragMode", JDesktopPane::getDragMode, JDesktopPane::setDragMode, dragMode);
    }

    public void selectedFrame(
        @Nullable DeclarativeComponentSupplier<? extends JInternalFrame> selectedFrame) {
      fnAttribute(PREFIX + "selectedFrame", JDesktopPane::getSelectedFrame, JDesktopPane::setSelectedFrame, selectedFrame);
    }
  }
}
