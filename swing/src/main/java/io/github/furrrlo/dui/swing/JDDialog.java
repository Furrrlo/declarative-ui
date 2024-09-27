package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.DeclarativeComponentSupplier;
import io.github.furrrlo.dui.IdentityFreeConsumer;
import io.github.furrrlo.dui.IdentityFreeSupplier;
import java.awt.Component;
import java.awt.Container;
import java.lang.Class;
import java.lang.Integer;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.function.Supplier;
import javax.swing.JDialog;
import javax.swing.JLayeredPane;
import javax.swing.JMenuBar;
import javax.swing.TransferHandler;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class JDDialog {
  public static DeclarativeComponent<JDialog> fn(IdentityFreeConsumer<Decorator<JDialog>> body) {
    return fn(JDialog.class, JDialog::new, body);
  }

  public static DeclarativeComponent<JDialog> fn(Supplier<JDialog> factory,
      IdentityFreeConsumer<Decorator<JDialog>> body) {
    return fn(JDialog.class, factory, body);
  }

  public static <T extends JDialog> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentityFreeConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JDialog> extends DAwtWindow.Decorator<T> {
    private static final String PREFIX = "__JDDialog__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void jMenuBar(@Nullable DeclarativeComponentSupplier<? extends JMenuBar> JMenuBar) {
      fnAttribute(PREFIX + "JMenuBar", JDialog::getJMenuBar, JDialog::setJMenuBar, JMenuBar);
    }

    public void contentPane(
        @Nullable DeclarativeComponentSupplier<? extends Container> contentPane) {
      fnAttribute(PREFIX + "contentPane", JDialog::getContentPane, JDialog::setContentPane, contentPane);
    }

    public void defaultCloseOperation(IdentityFreeSupplier<Integer> defaultCloseOperation) {
      attribute(PREFIX + "defaultCloseOperation", JDialog::getDefaultCloseOperation, JDialog::setDefaultCloseOperation, defaultCloseOperation);
    }

    public void glassPane(@Nullable DeclarativeComponentSupplier<? extends Component> glassPane) {
      fnAttribute(PREFIX + "glassPane", JDialog::getGlassPane, JDialog::setGlassPane, glassPane);
    }

    public void layeredPane(
        @Nullable DeclarativeComponentSupplier<? extends JLayeredPane> layeredPane) {
      fnAttribute(PREFIX + "layeredPane", JDialog::getLayeredPane, JDialog::setLayeredPane, layeredPane);
    }

    public void transferHandler(IdentityFreeSupplier<? extends TransferHandler> transferHandler) {
      attribute(PREFIX + "transferHandler", JDialog::getTransferHandler, JDialog::setTransferHandler, transferHandler);
    }
  }
}
