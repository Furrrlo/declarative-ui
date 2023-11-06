package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;
import io.leangen.geantyref.TypeFactory;
import io.leangen.geantyref.TypeToken;

import javax.swing.*;
import javax.swing.plaf.ListUI;
import java.awt.*;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDList {

  public static <E> DeclarativeComponent<JList<E>> fn(Class<E> type,
                                                      IdentifiableConsumer<Decorator<E, JList<E>>> body) {
    return fn(TypeToken.get(type), body);
  }

  @SuppressWarnings("unchecked")
  public static <E> DeclarativeComponent<JList<E>> fn(TypeToken<E> type,
                                                      IdentifiableConsumer<Decorator<E, JList<E>>> body) {
    return fn(
            (TypeToken<JList<E>>) TypeToken.get(TypeFactory.parameterizedClass(JList.class, type.getType())),
            JList::new,
            body);
  }

  public static <E, T extends JList<E>> DeclarativeComponent<T> fn(TypeToken<T> type,
                                                                   Supplier<T> factory,
                                                                   IdentifiableConsumer<Decorator<E, T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<E, T extends JList<E>> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__JDList__";

    protected Decorator(TypeToken<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void ui(Supplier<? extends ListUI> ui) {
      attribute(PREFIX + "ui", JList::getUI, JList::setUI, ui);
    }

    public void cellRenderer(Supplier<? extends ListCellRenderer<? super E>> cellRenderer) {
      this.<ListCellRenderer<? super E>>attribute(PREFIX + "cellRenderer", JList::getCellRenderer, JList::setCellRenderer, cellRenderer);
    }

    public void dragEnabled(Supplier<Boolean> dragEnabled) {
      attribute(PREFIX + "dragEnabled", JList::getDragEnabled, JList::setDragEnabled, dragEnabled);
    }

    public void dropMode(Supplier<DropMode> dropMode) {
      attribute(PREFIX + "dropMode", JList::getDropMode, JList::setDropMode, dropMode);
    }

    public void fixedCellHeight(Supplier<Integer> fixedCellHeight) {
      attribute(PREFIX + "fixedCellHeight", JList::getFixedCellHeight, JList::setFixedCellHeight, fixedCellHeight);
    }

    public void fixedCellWidth(Supplier<Integer> fixedCellWidth) {
      attribute(PREFIX + "fixedCellWidth", JList::getFixedCellWidth, JList::setFixedCellWidth, fixedCellWidth);
    }

    public void layoutOrientation(Supplier<Integer> layoutOrientation) {
      attribute(PREFIX + "layoutOrientation", JList::getLayoutOrientation, JList::setLayoutOrientation, layoutOrientation);
    }

    public void listData() {
      // TODO: implement "listData"
    }

    public void model(Supplier<? extends ListModel<E>> model) {
      attribute(PREFIX + "model", JList::getModel, JList::setModel, model);
    }

    public void prototypeCellValue(Supplier<E> prototypeCellValue) {
      attribute(PREFIX + "prototypeCellValue", JList::getPrototypeCellValue, JList::setPrototypeCellValue, prototypeCellValue);
    }

    public void selectedIndex(Supplier<Integer> selectedIndex) {
      attribute(PREFIX + "selectedIndex", JList::getSelectedIndex, JList::setSelectedIndex, selectedIndex);
    }

    public void selectedIndices() {
      // TODO: implement "selectedIndices"
    }

    public void selectionBackground(Supplier<? extends Color> selectionBackground) {
      attribute(PREFIX + "selectionBackground", JList::getSelectionBackground, JList::setSelectionBackground, selectionBackground);
    }

    public void selectionForeground(Supplier<? extends Color> selectionForeground) {
      attribute(PREFIX + "selectionForeground", JList::getSelectionForeground, JList::setSelectionForeground, selectionForeground);
    }

    public void selectionMode(Supplier<Integer> selectionMode) {
      attribute(PREFIX + "selectionMode", JList::getSelectionMode, JList::setSelectionMode, selectionMode);
    }

    public void selectionModel(Supplier<? extends ListSelectionModel> selectionModel) {
      attribute(PREFIX + "selectionModel", JList::getSelectionModel, JList::setSelectionModel, selectionModel);
    }

    public void valueIsAdjusting(Supplier<Boolean> valueIsAdjusting) {
      attribute(PREFIX + "valueIsAdjusting", JList::getValueIsAdjusting, JList::setValueIsAdjusting, valueIsAdjusting);
    }

    public void visibleRowCount(Supplier<Integer> visibleRowCount) {
      attribute(PREFIX + "visibleRowCount", JList::getVisibleRowCount, JList::setVisibleRowCount, visibleRowCount);
    }
  }
}
