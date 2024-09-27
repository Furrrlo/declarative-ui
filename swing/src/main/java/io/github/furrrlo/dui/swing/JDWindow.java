package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.DeclarativeComponentSupplier;
import io.github.furrrlo.dui.IdentityFreeConsumer;
import io.github.furrrlo.dui.IdentityFreeSupplier;
import java.awt.Component;
import java.awt.Container;
import java.lang.Class;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.function.Supplier;
import javax.swing.JLayeredPane;
import javax.swing.JWindow;
import javax.swing.TransferHandler;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class JDWindow {
  public static DeclarativeComponent<JWindow> fn(IdentityFreeConsumer<Decorator<JWindow>> body) {
    return fn(JWindow.class, JWindow::new, body);
  }

  public static DeclarativeComponent<JWindow> fn(Supplier<JWindow> factory,
      IdentityFreeConsumer<Decorator<JWindow>> body) {
    return fn(JWindow.class, factory, body);
  }

  public static <T extends JWindow> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentityFreeConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JWindow> extends DAwtWindow.Decorator<T> {
    private static final String PREFIX = "__JDWindow__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void contentPane(
        @Nullable DeclarativeComponentSupplier<? extends Container> contentPane) {
      fnAttribute(PREFIX + "contentPane", JWindow::getContentPane, JWindow::setContentPane, contentPane);
    }

    public void glassPane(@Nullable DeclarativeComponentSupplier<? extends Component> glassPane) {
      fnAttribute(PREFIX + "glassPane", JWindow::getGlassPane, JWindow::setGlassPane, glassPane);
    }

    public void layeredPane(
        @Nullable DeclarativeComponentSupplier<? extends JLayeredPane> layeredPane) {
      fnAttribute(PREFIX + "layeredPane", JWindow::getLayeredPane, JWindow::setLayeredPane, layeredPane);
    }

    public void transferHandler(IdentityFreeSupplier<? extends TransferHandler> transferHandler) {
      attribute(PREFIX + "transferHandler", JWindow::getTransferHandler, JWindow::setTransferHandler, transferHandler);
    }
  }
}
