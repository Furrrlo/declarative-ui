package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentityFreeConsumer;
import io.github.furrrlo.dui.IdentityFreeSupplier;
import io.leangen.geantyref.TypeFactory;
import io.leangen.geantyref.TypeToken;

import javax.swing.*;
import javax.swing.plaf.ListUI;
import java.awt.*;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDList {

  public static <E> DeclarativeComponent<JList<E>> fn(Class<E> type,
                                                      IdentityFreeConsumer<Decorator<E, JList<E>>> body) {
    return fn(TypeToken.get(type), body);
  }

  @SuppressWarnings("unchecked")
  public static <E> DeclarativeComponent<JList<E>> fn(TypeToken<E> type,
                                                      IdentityFreeConsumer<Decorator<E, JList<E>>> body) {
    return fn(
            (TypeToken<JList<E>>) TypeToken.get(TypeFactory.parameterizedClass(JList.class, type.getType())),
            JList::new,
            body);
  }

  public static <E, T extends JList<E>> DeclarativeComponent<T> fn(TypeToken<T> type,
                                                                   Supplier<T> factory,
                                                                   IdentityFreeConsumer<Decorator<E, T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<E, T extends JList<E>> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__JDList__";

    protected Decorator(TypeToken<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void ui(IdentityFreeSupplier<? extends ListUI> ui) {
      attribute(PREFIX + "ui", JList::getUI, JList::setUI, ui);
    }

    public void cellRenderer(IdentityFreeSupplier<? extends ListCellRenderer<? super E>> cellRenderer) {
      this.<ListCellRenderer<? super E>>attribute(PREFIX + "cellRenderer", JList::getCellRenderer, JList::setCellRenderer, cellRenderer);
    }

    public void dragEnabled(IdentityFreeSupplier<Boolean> dragEnabled) {
      attribute(PREFIX + "dragEnabled", JList::getDragEnabled, JList::setDragEnabled, dragEnabled);
    }

    public void dropMode(IdentityFreeSupplier<DropMode> dropMode) {
      attribute(PREFIX + "dropMode", JList::getDropMode, JList::setDropMode, dropMode);
    }

    public void fixedCellHeight(IdentityFreeSupplier<Integer> fixedCellHeight) {
      attribute(PREFIX + "fixedCellHeight", JList::getFixedCellHeight, JList::setFixedCellHeight, fixedCellHeight);
    }

    public void fixedCellWidth(IdentityFreeSupplier<Integer> fixedCellWidth) {
      attribute(PREFIX + "fixedCellWidth", JList::getFixedCellWidth, JList::setFixedCellWidth, fixedCellWidth);
    }

    public void layoutOrientation(IdentityFreeSupplier<Integer> layoutOrientation) {
      attribute(PREFIX + "layoutOrientation", JList::getLayoutOrientation, JList::setLayoutOrientation, layoutOrientation);
    }

    public void listData() {
      // TODO: implement "listData"
    }

    public void model(IdentityFreeSupplier<? extends ListModel<E>> model) {
      attribute(PREFIX + "model", JList::getModel, JList::setModel, model);
    }

    public void prototypeCellValue(IdentityFreeSupplier<E> prototypeCellValue) {
      attribute(PREFIX + "prototypeCellValue", JList::getPrototypeCellValue, JList::setPrototypeCellValue, prototypeCellValue);
    }

    public void selectedIndex(IdentityFreeSupplier<Integer> selectedIndex) {
      attribute(PREFIX + "selectedIndex", JList::getSelectedIndex, JList::setSelectedIndex, selectedIndex);
    }

    public void selectedIndices() {
      // TODO: implement "selectedIndices"
    }

    public void selectionBackground(IdentityFreeSupplier<? extends Color> selectionBackground) {
      attribute(PREFIX + "selectionBackground", JList::getSelectionBackground, JList::setSelectionBackground, selectionBackground);
    }

    public void selectionForeground(IdentityFreeSupplier<? extends Color> selectionForeground) {
      attribute(PREFIX + "selectionForeground", JList::getSelectionForeground, JList::setSelectionForeground, selectionForeground);
    }

    public void selectionMode(IdentityFreeSupplier<Integer> selectionMode) {
      attribute(PREFIX + "selectionMode", JList::getSelectionMode, JList::setSelectionMode, selectionMode);
    }

    public void selectionModel(IdentityFreeSupplier<? extends ListSelectionModel> selectionModel) {
      attribute(PREFIX + "selectionModel", JList::getSelectionModel, JList::setSelectionModel, selectionModel);
    }

    public void valueIsAdjusting(IdentityFreeSupplier<Boolean> valueIsAdjusting) {
      attribute(PREFIX + "valueIsAdjusting", JList::getValueIsAdjusting, JList::setValueIsAdjusting, valueIsAdjusting);
    }

    public void visibleRowCount(IdentityFreeSupplier<Integer> visibleRowCount) {
      attribute(PREFIX + "visibleRowCount", JList::getVisibleRowCount, JList::setVisibleRowCount, visibleRowCount);
    }
  }
}
