package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;

import javax.swing.*;
import javax.swing.plaf.ListUI;
import java.awt.*;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDList {

  // TODO: what generics do we use here?

  public static DeclarativeComponent<JList> fn(IdentifiableConsumer<Decorator<JList>> body) {
    return fn(JList.class, JList::new, body);
  }

  public static DeclarativeComponent<JList> fn(Supplier<JList> factory,
      IdentifiableConsumer<Decorator<JList>> body) {
    return fn(JList.class, factory, body);
  }

  public static <T extends JList> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentifiableConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JList> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__JDList__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void ui(Supplier<? extends ListUI> ui) {
      attribute(PREFIX + "ui", JList::getUI, JList::setUI, ui);
    }

    public void cellRenderer(Supplier<? extends ListCellRenderer> cellRenderer) {
      attribute(PREFIX + "cellRenderer", JList::getCellRenderer, JList::setCellRenderer, cellRenderer);
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

    public void model(Supplier<? extends ListModel> model) {
      attribute(PREFIX + "model", JList::getModel, JList::setModel, model);
    }

    public void prototypeCellValue(Supplier<?> prototypeCellValue) {
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
