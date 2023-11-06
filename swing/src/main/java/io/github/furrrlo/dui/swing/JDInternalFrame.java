package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.DeclarativeComponentSupplier;
import io.github.furrrlo.dui.IdentifiableConsumer;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.Integer;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.function.Supplier;
import javax.swing.Icon;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenuBar;
import javax.swing.plaf.InternalFrameUI;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class JDInternalFrame {
  public static DeclarativeComponent<JInternalFrame> fn(
      IdentifiableConsumer<Decorator<JInternalFrame>> body) {
    return fn(JInternalFrame.class, JInternalFrame::new, body);
  }

  public static DeclarativeComponent<JInternalFrame> fn(Supplier<JInternalFrame> factory,
      IdentifiableConsumer<Decorator<JInternalFrame>> body) {
    return fn(JInternalFrame.class, factory, body);
  }

  public static <T extends JInternalFrame> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentifiableConsumer<Decorator<T>> body) {
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

    public void ui(Supplier<? extends InternalFrameUI> ui) {
      attribute(PREFIX + "ui", JInternalFrame::getUI, JInternalFrame::setUI, ui);
    }

    public void closable(Supplier<Boolean> closable) {
      attribute(PREFIX + "closable", JInternalFrame::isClosable, JInternalFrame::setClosable, closable);
    }

    public void closed(Supplier<Boolean> closed) {
      attribute(PREFIX + "closed", JInternalFrame::isClosed, JInternalFrame::setClosed, closed);
    }

    public void contentPane(
        @Nullable DeclarativeComponentSupplier<? extends Container> contentPane) {
      fnAttribute(PREFIX + "contentPane", JInternalFrame::getContentPane, JInternalFrame::setContentPane, contentPane);
    }

    public void cursor(Supplier<? extends Cursor> cursor) {
      attribute(PREFIX + "cursor", JInternalFrame::setCursor, cursor);
    }

    public void defaultCloseOperation(Supplier<Integer> defaultCloseOperation) {
      attribute(PREFIX + "defaultCloseOperation", JInternalFrame::getDefaultCloseOperation, JInternalFrame::setDefaultCloseOperation, defaultCloseOperation);
    }

    public void desktopIcon(
        @Nullable DeclarativeComponentSupplier<? extends JInternalFrame.JDesktopIcon> desktopIcon) {
      fnAttribute(PREFIX + "desktopIcon", JInternalFrame::getDesktopIcon, JInternalFrame::setDesktopIcon, desktopIcon);
    }

    public void focusCycleRoot(Supplier<Boolean> focusCycleRoot) {
      attribute(PREFIX + "focusCycleRoot", JInternalFrame::isFocusCycleRoot, JInternalFrame::setFocusCycleRoot, focusCycleRoot);
    }

    public void frameIcon(Supplier<? extends Icon> frameIcon) {
      attribute(PREFIX + "frameIcon", JInternalFrame::getFrameIcon, JInternalFrame::setFrameIcon, frameIcon);
    }

    public void glassPane(@Nullable DeclarativeComponentSupplier<? extends Component> glassPane) {
      fnAttribute(PREFIX + "glassPane", JInternalFrame::getGlassPane, JInternalFrame::setGlassPane, glassPane);
    }

    public void icon(Supplier<Boolean> icon) {
      attribute(PREFIX + "icon", JInternalFrame::isIcon, JInternalFrame::setIcon, icon);
    }

    public void iconifiable(Supplier<Boolean> iconifiable) {
      attribute(PREFIX + "iconifiable", JInternalFrame::isIconifiable, JInternalFrame::setIconifiable, iconifiable);
    }

    public void layer(Supplier<Integer> layer) {
      attribute(PREFIX + "layer", JInternalFrame::getLayer, JInternalFrame::setLayer, layer);
    }

    public void layeredPane(
        @Nullable DeclarativeComponentSupplier<? extends JLayeredPane> layeredPane) {
      fnAttribute(PREFIX + "layeredPane", JInternalFrame::getLayeredPane, JInternalFrame::setLayeredPane, layeredPane);
    }

    public void layout(Supplier<? extends LayoutManager> layout) {
      attribute(PREFIX + "layout", JInternalFrame::setLayout, layout);
    }

    public void maximizable(Supplier<Boolean> maximizable) {
      attribute(PREFIX + "maximizable", JInternalFrame::isMaximizable, JInternalFrame::setMaximizable, maximizable);
    }

    public void maximum(Supplier<Boolean> maximum) {
      attribute(PREFIX + "maximum", JInternalFrame::isMaximum, JInternalFrame::setMaximum, maximum);
    }

    public void normalBounds(Supplier<? extends Rectangle> normalBounds) {
      attribute(PREFIX + "normalBounds", JInternalFrame::getNormalBounds, JInternalFrame::setNormalBounds, normalBounds);
    }

    public void resizable(Supplier<Boolean> resizable) {
      attribute(PREFIX + "resizable", JInternalFrame::isResizable, JInternalFrame::setResizable, resizable);
    }

    public void selected(Supplier<Boolean> selected) {
      attribute(PREFIX + "selected", JInternalFrame::isSelected, JInternalFrame::setSelected, selected);
    }

    public void title(Supplier<String> title) {
      attribute(PREFIX + "title", JInternalFrame::getTitle, JInternalFrame::setTitle, title);
    }
  }
}
