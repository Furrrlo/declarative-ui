package io.github.furrrlo.dui.swing.tree;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;
import io.github.furrrlo.dui.swing.JDLabel;
import java.awt.Color;
import java.awt.Font;
import java.lang.Class;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.function.Supplier;
import javax.swing.Icon;
import javax.swing.tree.DefaultTreeCellRenderer;

@SuppressWarnings("unused")
public class DDefaultTreeCellRenderer {
  public static DeclarativeComponent<DefaultTreeCellRenderer> fn(
      IdentifiableConsumer<Decorator<DefaultTreeCellRenderer>> body) {
    return fn(DefaultTreeCellRenderer.class, DefaultTreeCellRenderer::new, body);
  }

  public static DeclarativeComponent<DefaultTreeCellRenderer> fn(Supplier<DefaultTreeCellRenderer> factory,
      IdentifiableConsumer<Decorator<DefaultTreeCellRenderer>> body) {
    return fn(DefaultTreeCellRenderer.class, factory, body);
  }

  public static <T extends DefaultTreeCellRenderer> DeclarativeComponent<T> fn(Class<T> type,
      Supplier<T> factory, IdentifiableConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends DefaultTreeCellRenderer> extends JDLabel.Decorator<T> {
    private static final String PREFIX = "__DDefaultTreeCellRenderer__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void background(Supplier<? extends Color> background) {
      attribute(PREFIX + "background", DefaultTreeCellRenderer::setBackground, background);
    }

    public void backgroundNonSelectionColor(Supplier<? extends Color> backgroundNonSelectionColor) {
      attribute(PREFIX + "backgroundNonSelectionColor", DefaultTreeCellRenderer::getBackgroundNonSelectionColor, DefaultTreeCellRenderer::setBackgroundNonSelectionColor, backgroundNonSelectionColor);
    }

    public void backgroundSelectionColor(Supplier<? extends Color> backgroundSelectionColor) {
      attribute(PREFIX + "backgroundSelectionColor", DefaultTreeCellRenderer::getBackgroundSelectionColor, DefaultTreeCellRenderer::setBackgroundSelectionColor, backgroundSelectionColor);
    }

    public void borderSelectionColor(Supplier<? extends Color> borderSelectionColor) {
      attribute(PREFIX + "borderSelectionColor", DefaultTreeCellRenderer::getBorderSelectionColor, DefaultTreeCellRenderer::setBorderSelectionColor, borderSelectionColor);
    }

    public void closedIcon(Supplier<? extends Icon> closedIcon) {
      attribute(PREFIX + "closedIcon", DefaultTreeCellRenderer::getClosedIcon, DefaultTreeCellRenderer::setClosedIcon, closedIcon);
    }

    public void font(Supplier<? extends Font> font) {
      attribute(PREFIX + "font", DefaultTreeCellRenderer::getFont, DefaultTreeCellRenderer::setFont, font);
    }

    public void leafIcon(Supplier<? extends Icon> leafIcon) {
      attribute(PREFIX + "leafIcon", DefaultTreeCellRenderer::getLeafIcon, DefaultTreeCellRenderer::setLeafIcon, leafIcon);
    }

    public void openIcon(Supplier<? extends Icon> openIcon) {
      attribute(PREFIX + "openIcon", DefaultTreeCellRenderer::getOpenIcon, DefaultTreeCellRenderer::setOpenIcon, openIcon);
    }

    public void textNonSelectionColor(Supplier<? extends Color> textNonSelectionColor) {
      attribute(PREFIX + "textNonSelectionColor", DefaultTreeCellRenderer::getTextNonSelectionColor, DefaultTreeCellRenderer::setTextNonSelectionColor, textNonSelectionColor);
    }

    public void textSelectionColor(Supplier<? extends Color> textSelectionColor) {
      attribute(PREFIX + "textSelectionColor", DefaultTreeCellRenderer::getTextSelectionColor, DefaultTreeCellRenderer::setTextSelectionColor, textSelectionColor);
    }
  }
}
