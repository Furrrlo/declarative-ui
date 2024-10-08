package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.*;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import javax.swing.plaf.InternalFrameUI;
import java.awt.*;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDInternalFrame {
  public static DeclarativeComponent<JInternalFrame> fn(
      IdentityFreeConsumer<Decorator<JInternalFrame>> body) {
    return fn(JInternalFrame.class, JInternalFrame::new, body);
  }

  public static DeclarativeComponent<JInternalFrame> fn(Supplier<JInternalFrame> factory,
      IdentityFreeConsumer<Decorator<JInternalFrame>> body) {
    return fn(JInternalFrame.class, factory, body);
  }

  public static <T extends JInternalFrame> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentityFreeConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JInternalFrame> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__JDInternalFrame__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void JMenuBar(@Nullable DeclarativeComponentSupplier<? extends JMenuBar> JMenuBar) {
      fnAttribute(PREFIX + "JMenuBar", JInternalFrame::getJMenuBar, JInternalFrame::setJMenuBar, JMenuBar);
    }

    public void ui(IdentityFreeSupplier<? extends InternalFrameUI> ui) {
      attribute(PREFIX + "ui", JInternalFrame::getUI, JInternalFrame::setUI, ui);
    }

    public void closable(IdentityFreeSupplier<Boolean> closable) {
      attribute(PREFIX + "closable", JInternalFrame::isClosable, JInternalFrame::setClosable, closable);
    }

    public void closed(Supplier<Boolean> closed) {
      // TODO: what do I do here?
      throw new UnsupportedOperationException();
//      attribute(PREFIX + "closed", JInternalFrame::isClosed, JInternalFrame::setClosed, closed);
    }

    public void contentPane(
        @Nullable DeclarativeComponentSupplier<? extends Container> contentPane) {
      fnAttribute(PREFIX + "contentPane", JInternalFrame::getContentPane, JInternalFrame::setContentPane, contentPane);
    }

    public void cursor(IdentityFreeSupplier<? extends Cursor> cursor) {
      attribute(PREFIX + "cursor", JInternalFrame::getCursor, JInternalFrame::setCursor, cursor);
    }

    public void defaultCloseOperation(IdentityFreeSupplier<Integer> defaultCloseOperation) {
      attribute(PREFIX + "defaultCloseOperation", JInternalFrame::getDefaultCloseOperation, JInternalFrame::setDefaultCloseOperation, defaultCloseOperation);
    }

    public void desktopIcon(
        @Nullable DeclarativeComponentSupplier<? extends JInternalFrame.JDesktopIcon> desktopIcon) {
      fnAttribute(PREFIX + "desktopIcon", JInternalFrame::getDesktopIcon, JInternalFrame::setDesktopIcon, desktopIcon);
    }

    public void focusCycleRoot(IdentityFreeSupplier<Boolean> focusCycleRoot) {
      attribute(PREFIX + "focusCycleRoot", JInternalFrame::isFocusCycleRoot, JInternalFrame::setFocusCycleRoot, focusCycleRoot);
    }

    public void frameIcon(IdentityFreeSupplier<? extends Icon> frameIcon) {
      attribute(PREFIX + "frameIcon", JInternalFrame::getFrameIcon, JInternalFrame::setFrameIcon, frameIcon);
    }

    public void glassPane(@Nullable DeclarativeComponentSupplier<? extends Component> glassPane) {
      fnAttribute(PREFIX + "glassPane", JInternalFrame::getGlassPane, JInternalFrame::setGlassPane, glassPane);
    }

    public void icon(Supplier<Boolean> icon) {
      // TODO: what do I do here?
      throw new UnsupportedOperationException();
//      attribute(PREFIX + "icon", JInternalFrame::isIcon, JInternalFrame::setIcon, icon);
    }

    public void iconifiable(IdentityFreeSupplier<Boolean> iconifiable) {
      attribute(PREFIX + "iconifiable", JInternalFrame::isIconifiable, JInternalFrame::setIconifiable, iconifiable);
    }

    public void layer(IdentityFreeSupplier<Integer> layer) {
      attribute(PREFIX + "layer", JInternalFrame::getLayer, JInternalFrame::setLayer, layer);
    }

    public void layeredPane(
        @Nullable DeclarativeComponentSupplier<? extends JLayeredPane> layeredPane) {
      fnAttribute(PREFIX + "layeredPane", JInternalFrame::getLayeredPane, JInternalFrame::setLayeredPane, layeredPane);
    }

    public void maximizable(IdentityFreeSupplier<Boolean> maximizable) {
      attribute(PREFIX + "maximizable", JInternalFrame::isMaximizable, JInternalFrame::setMaximizable, maximizable);
    }

    public void maximum(Supplier<Boolean> maximum) {
      // TODO: what do I do here?
      throw new UnsupportedOperationException();
//      attribute(PREFIX + "maximum", JInternalFrame::isMaximum, JInternalFrame::setMaximum, maximum);
    }

    public void normalBounds(IdentityFreeSupplier<? extends Rectangle> normalBounds) {
      attribute(PREFIX + "normalBounds", JInternalFrame::getNormalBounds, JInternalFrame::setNormalBounds, normalBounds);
    }

    public void resizable(IdentityFreeSupplier<Boolean> resizable) {
      attribute(PREFIX + "resizable", JInternalFrame::isResizable, JInternalFrame::setResizable, resizable);
    }

    public void selected(Supplier<Boolean> selected) {
      // TODO: what do I do here?
      throw new UnsupportedOperationException();
//      attribute(PREFIX + "selected", JInternalFrame::isSelected, JInternalFrame::setSelected, selected);
    }

    public void title(IdentityFreeSupplier<String> title) {
      attribute(PREFIX + "title", JInternalFrame::getTitle, JInternalFrame::setTitle, title);
    }
  }
}
