package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.DeclarativeComponentSupplier;
import io.github.furrrlo.dui.IdentifiableConsumer;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.plaf.TableUI;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDTable {
  public static DeclarativeComponent<JTable> fn(IdentifiableConsumer<Decorator<JTable>> body) {
    return fn(JTable.class, JTable::new, body);
  }

  public static DeclarativeComponent<JTable> fn(Supplier<JTable> factory,
      IdentifiableConsumer<Decorator<JTable>> body) {
    return fn(JTable.class, factory, body);
  }

  public static <T extends JTable> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentifiableConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JTable> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__JDTable__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void ui(Supplier<? extends TableUI> ui) {
      attribute(PREFIX + "ui", JTable::getUI, JTable::setUI, ui);
    }

    public void autoCreateColumnsFromModel(Supplier<Boolean> autoCreateColumnsFromModel) {
      attribute(PREFIX + "autoCreateColumnsFromModel", JTable::getAutoCreateColumnsFromModel, JTable::setAutoCreateColumnsFromModel, autoCreateColumnsFromModel);
    }

    public void autoCreateRowSorter(Supplier<Boolean> autoCreateRowSorter) {
      attribute(PREFIX + "autoCreateRowSorter", JTable::getAutoCreateRowSorter, JTable::setAutoCreateRowSorter, autoCreateRowSorter);
    }

    public void autoResizeMode(Supplier<Integer> autoResizeMode) {
      attribute(PREFIX + "autoResizeMode", JTable::getAutoResizeMode, JTable::setAutoResizeMode, autoResizeMode);
    }

    public void cellEditor(Supplier<? extends TableCellEditor> cellEditor) {
      attribute(PREFIX + "cellEditor", JTable::getCellEditor, JTable::setCellEditor, cellEditor);
    }

    public void cellSelectionEnabled(Supplier<Boolean> cellSelectionEnabled) {
      attribute(PREFIX + "cellSelectionEnabled", JTable::getCellSelectionEnabled, JTable::setCellSelectionEnabled, cellSelectionEnabled);
    }

    public void columnModel(Supplier<? extends TableColumnModel> columnModel) {
      attribute(PREFIX + "columnModel", JTable::getColumnModel, JTable::setColumnModel, columnModel);
    }

    public void columnSelectionAllowed(Supplier<Boolean> columnSelectionAllowed) {
      attribute(PREFIX + "columnSelectionAllowed", JTable::getColumnSelectionAllowed, JTable::setColumnSelectionAllowed, columnSelectionAllowed);
    }

    public void dragEnabled(Supplier<Boolean> dragEnabled) {
      attribute(PREFIX + "dragEnabled", JTable::getDragEnabled, JTable::setDragEnabled, dragEnabled);
    }

    public void dropMode(Supplier<DropMode> dropMode) {
      attribute(PREFIX + "dropMode", JTable::getDropMode, JTable::setDropMode, dropMode);
    }

    public void editingColumn(Supplier<Integer> editingColumn) {
      attribute(PREFIX + "editingColumn", JTable::getEditingColumn, JTable::setEditingColumn, editingColumn);
    }

    public void editingRow(Supplier<Integer> editingRow) {
      attribute(PREFIX + "editingRow", JTable::getEditingRow, JTable::setEditingRow, editingRow);
    }

    public void fillsViewportHeight(Supplier<Boolean> fillsViewportHeight) {
      attribute(PREFIX + "fillsViewportHeight", JTable::getFillsViewportHeight, JTable::setFillsViewportHeight, fillsViewportHeight);
    }

    public void gridColor(Supplier<? extends Color> gridColor) {
      attribute(PREFIX + "gridColor", JTable::getGridColor, JTable::setGridColor, gridColor);
    }

    public void intercellSpacing(Supplier<? extends Dimension> intercellSpacing) {
      attribute(PREFIX + "intercellSpacing", JTable::getIntercellSpacing, JTable::setIntercellSpacing, intercellSpacing);
    }

    public void model(Supplier<? extends TableModel> model) {
      attribute(PREFIX + "model", JTable::getModel, JTable::setModel, model);
    }

    public void preferredScrollableViewportSize(
        Supplier<? extends Dimension> preferredScrollableViewportSize) {
      attribute(PREFIX + "preferredScrollableViewportSize", JTable::getPreferredScrollableViewportSize, JTable::setPreferredScrollableViewportSize, preferredScrollableViewportSize);
    }

    public void rowHeight(Supplier<Integer> rowHeight) {
      attribute(PREFIX + "rowHeight", JTable::getRowHeight, JTable::setRowHeight, rowHeight);
    }

    public void rowMargin(Supplier<Integer> rowMargin) {
      attribute(PREFIX + "rowMargin", JTable::getRowMargin, JTable::setRowMargin, rowMargin);
    }

    public void rowSelectionAllowed(Supplier<Boolean> rowSelectionAllowed) {
      attribute(PREFIX + "rowSelectionAllowed", JTable::getRowSelectionAllowed, JTable::setRowSelectionAllowed, rowSelectionAllowed);
    }

    public void rowSorter(Supplier<? extends RowSorter<? extends TableModel>> rowSorter) {
      attribute(PREFIX + "rowSorter", JTable::getRowSorter, JTable::setRowSorter, rowSorter);
    }

    public void selectionBackground(Supplier<? extends Color> selectionBackground) {
      attribute(PREFIX + "selectionBackground", JTable::getSelectionBackground, JTable::setSelectionBackground, selectionBackground);
    }

    public void selectionForeground(Supplier<? extends Color> selectionForeground) {
      attribute(PREFIX + "selectionForeground", JTable::getSelectionForeground, JTable::setSelectionForeground, selectionForeground);
    }

    public void selectionMode(Supplier<Integer> selectionMode) {
      attribute(PREFIX + "selectionMode", JTable::setSelectionMode, selectionMode);
    }

    public void selectionModel(Supplier<? extends ListSelectionModel> selectionModel) {
      attribute(PREFIX + "selectionModel", JTable::getSelectionModel, JTable::setSelectionModel, selectionModel);
    }

    public void showGrid(Supplier<Boolean> showGrid) {
      attribute(PREFIX + "showGrid", JTable::setShowGrid, showGrid);
    }

    public void showHorizontalLines(Supplier<Boolean> showHorizontalLines) {
      attribute(PREFIX + "showHorizontalLines", JTable::getShowHorizontalLines, JTable::setShowHorizontalLines, showHorizontalLines);
    }

    public void showVerticalLines(Supplier<Boolean> showVerticalLines) {
      attribute(PREFIX + "showVerticalLines", JTable::getShowVerticalLines, JTable::setShowVerticalLines, showVerticalLines);
    }

    public void surrendersFocusOnKeystroke(Supplier<Boolean> surrendersFocusOnKeystroke) {
      attribute(PREFIX + "surrendersFocusOnKeystroke", JTable::getSurrendersFocusOnKeystroke, JTable::setSurrendersFocusOnKeystroke, surrendersFocusOnKeystroke);
    }

    public void tableHeader(
        @Nullable DeclarativeComponentSupplier<? extends JTableHeader> tableHeader) {
      fnAttribute(PREFIX + "tableHeader", JTable::getTableHeader, JTable::setTableHeader, tableHeader);
    }

    public void updateSelectionOnSort(Supplier<Boolean> updateSelectionOnSort) {
      attribute(PREFIX + "updateSelectionOnSort", JTable::getUpdateSelectionOnSort, JTable::setUpdateSelectionOnSort, updateSelectionOnSort);
    }
  }
}
