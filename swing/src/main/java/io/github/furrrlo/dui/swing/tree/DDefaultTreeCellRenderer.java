package io.github.furrrlo.dui.swing.tree;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentityFreeConsumer;
import io.github.furrrlo.dui.IdentityFreeSupplier;
import io.github.furrrlo.dui.swing.JDLabel;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class DDefaultTreeCellRenderer {
  public static DeclarativeComponent<DefaultTreeCellRenderer> fn(
      IdentityFreeConsumer<Decorator<DefaultTreeCellRenderer>> body) {
    return fn(DefaultTreeCellRenderer.class, DefaultTreeCellRenderer::new, body);
  }

  public static DeclarativeComponent<DefaultTreeCellRenderer> fn(Supplier<DefaultTreeCellRenderer> factory,
      IdentityFreeConsumer<Decorator<DefaultTreeCellRenderer>> body) {
    return fn(DefaultTreeCellRenderer.class, factory, body);
  }

  public static <T extends DefaultTreeCellRenderer> DeclarativeComponent<T> fn(Class<T> type,
      Supplier<T> factory, IdentityFreeConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends DefaultTreeCellRenderer> extends JDLabel.Decorator<T> {
    private static final String PREFIX = "__DDefaultTreeCellRenderer__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void backgroundNonSelectionColor(IdentityFreeSupplier<? extends Color> backgroundNonSelectionColor) {
      attribute(PREFIX + "backgroundNonSelectionColor", DefaultTreeCellRenderer::getBackgroundNonSelectionColor, DefaultTreeCellRenderer::setBackgroundNonSelectionColor, backgroundNonSelectionColor);
    }

    public void backgroundSelectionColor(IdentityFreeSupplier<? extends Color> backgroundSelectionColor) {
      attribute(PREFIX + "backgroundSelectionColor", DefaultTreeCellRenderer::getBackgroundSelectionColor, DefaultTreeCellRenderer::setBackgroundSelectionColor, backgroundSelectionColor);
    }

    public void borderSelectionColor(IdentityFreeSupplier<? extends Color> borderSelectionColor) {
      attribute(PREFIX + "borderSelectionColor", DefaultTreeCellRenderer::getBorderSelectionColor, DefaultTreeCellRenderer::setBorderSelectionColor, borderSelectionColor);
    }

    public void closedIcon(IdentityFreeSupplier<? extends Icon> closedIcon) {
      attribute(PREFIX + "closedIcon", DefaultTreeCellRenderer::getClosedIcon, DefaultTreeCellRenderer::setClosedIcon, closedIcon);
    }

    public void leafIcon(IdentityFreeSupplier<? extends Icon> leafIcon) {
      attribute(PREFIX + "leafIcon", DefaultTreeCellRenderer::getLeafIcon, DefaultTreeCellRenderer::setLeafIcon, leafIcon);
    }

    public void openIcon(IdentityFreeSupplier<? extends Icon> openIcon) {
      attribute(PREFIX + "openIcon", DefaultTreeCellRenderer::getOpenIcon, DefaultTreeCellRenderer::setOpenIcon, openIcon);
    }

    public void textNonSelectionColor(IdentityFreeSupplier<? extends Color> textNonSelectionColor) {
      attribute(PREFIX + "textNonSelectionColor", DefaultTreeCellRenderer::getTextNonSelectionColor, DefaultTreeCellRenderer::setTextNonSelectionColor, textNonSelectionColor);
    }

    public void textSelectionColor(IdentityFreeSupplier<? extends Color> textSelectionColor) {
      attribute(PREFIX + "textSelectionColor", DefaultTreeCellRenderer::getTextSelectionColor, DefaultTreeCellRenderer::setTextSelectionColor, textSelectionColor);
    }
  }
}
