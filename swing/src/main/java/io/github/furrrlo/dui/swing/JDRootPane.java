package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.DeclarativeComponentSupplier;
import io.github.furrrlo.dui.IdentifiableConsumer;
import java.awt.Component;
import java.awt.Container;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.Integer;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.function.Supplier;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import javax.swing.plaf.RootPaneUI;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class JDRootPane {
  public static DeclarativeComponent<JRootPane> fn(IdentifiableConsumer<Decorator<JRootPane>> body) {
    return fn(JRootPane.class, JRootPane::new, body);
  }

  public static DeclarativeComponent<JRootPane> fn(Supplier<JRootPane> factory,
      IdentifiableConsumer<Decorator<JRootPane>> body) {
    return fn(JRootPane.class, factory, body);
  }

  public static <T extends JRootPane> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentifiableConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JRootPane> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__JDRootPane__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void JMenuBar(@Nullable DeclarativeComponentSupplier<? extends JMenuBar> JMenuBar) {
      fnAttribute(PREFIX + "JMenuBar", JRootPane::getJMenuBar, JRootPane::setJMenuBar, JMenuBar);
    }

    public void ui(Supplier<? extends RootPaneUI> ui) {
      attribute(PREFIX + "ui", JRootPane::getUI, JRootPane::setUI, ui);
    }

    public void contentPane(
        @Nullable DeclarativeComponentSupplier<? extends Container> contentPane) {
      fnAttribute(PREFIX + "contentPane", JRootPane::getContentPane, JRootPane::setContentPane, contentPane);
    }

    public void defaultButton(
        @Nullable DeclarativeComponentSupplier<? extends JButton> defaultButton) {
      fnAttribute(PREFIX + "defaultButton", JRootPane::getDefaultButton, JRootPane::setDefaultButton, defaultButton);
    }

    public void doubleBuffered(Supplier<Boolean> doubleBuffered) {
      attribute(PREFIX + "doubleBuffered", JRootPane::setDoubleBuffered, doubleBuffered);
    }

    public void glassPane(@Nullable DeclarativeComponentSupplier<? extends Component> glassPane) {
      fnAttribute(PREFIX + "glassPane", JRootPane::getGlassPane, JRootPane::setGlassPane, glassPane);
    }

    public void layeredPane(
        @Nullable DeclarativeComponentSupplier<? extends JLayeredPane> layeredPane) {
      fnAttribute(PREFIX + "layeredPane", JRootPane::getLayeredPane, JRootPane::setLayeredPane, layeredPane);
    }

    public void windowDecorationStyle(Supplier<Integer> windowDecorationStyle) {
      attribute(PREFIX + "windowDecorationStyle", JRootPane::getWindowDecorationStyle, JRootPane::setWindowDecorationStyle, windowDecorationStyle);
    }
  }
}
