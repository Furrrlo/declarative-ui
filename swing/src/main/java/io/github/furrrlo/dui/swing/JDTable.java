package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.*;
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

    public void ui(IdentifiableSupplier<? extends TableUI> ui) {
      attribute(PREFIX + "ui", JTable::getUI, JTable::setUI, ui);
    }

    public void autoCreateColumnsFromModel(IdentifiableSupplier<Boolean> autoCreateColumnsFromModel) {
      attribute(PREFIX + "autoCreateColumnsFromModel", JTable::getAutoCreateColumnsFromModel, JTable::setAutoCreateColumnsFromModel, autoCreateColumnsFromModel);
    }

    public void autoCreateRowSorter(IdentifiableSupplier<Boolean> autoCreateRowSorter) {
      attribute(PREFIX + "autoCreateRowSorter", JTable::getAutoCreateRowSorter, JTable::setAutoCreateRowSorter, autoCreateRowSorter);
    }

    public void autoResizeMode(IdentifiableSupplier<Integer> autoResizeMode) {
      attribute(PREFIX + "autoResizeMode", JTable::getAutoResizeMode, JTable::setAutoResizeMode, autoResizeMode);
    }

    public void cellEditor(IdentifiableSupplier<? extends TableCellEditor> cellEditor) {
      attribute(PREFIX + "cellEditor", JTable::getCellEditor, JTable::setCellEditor, cellEditor);
    }

    public void cellSelectionEnabled(IdentifiableSupplier<Boolean> cellSelectionEnabled) {
      attribute(PREFIX + "cellSelectionEnabled", JTable::getCellSelectionEnabled, JTable::setCellSelectionEnabled, cellSelectionEnabled);
    }

    public void columnModel(IdentifiableSupplier<? extends TableColumnModel> columnModel) {
      attribute(PREFIX + "columnModel", JTable::getColumnModel, JTable::setColumnModel, columnModel);
    }

    public void columnSelectionAllowed(IdentifiableSupplier<Boolean> columnSelectionAllowed) {
      attribute(PREFIX + "columnSelectionAllowed", JTable::getColumnSelectionAllowed, JTable::setColumnSelectionAllowed, columnSelectionAllowed);
    }

    public void dragEnabled(IdentifiableSupplier<Boolean> dragEnabled) {
      attribute(PREFIX + "dragEnabled", JTable::getDragEnabled, JTable::setDragEnabled, dragEnabled);
    }

    public void dropMode(IdentifiableSupplier<DropMode> dropMode) {
      attribute(PREFIX + "dropMode", JTable::getDropMode, JTable::setDropMode, dropMode);
    }

    public void editingColumn(IdentifiableSupplier<Integer> editingColumn) {
      attribute(PREFIX + "editingColumn", JTable::getEditingColumn, JTable::setEditingColumn, editingColumn);
    }

    public void editingRow(IdentifiableSupplier<Integer> editingRow) {
      attribute(PREFIX + "editingRow", JTable::getEditingRow, JTable::setEditingRow, editingRow);
    }

    public void fillsViewportHeight(IdentifiableSupplier<Boolean> fillsViewportHeight) {
      attribute(PREFIX + "fillsViewportHeight", JTable::getFillsViewportHeight, JTable::setFillsViewportHeight, fillsViewportHeight);
    }

    public void gridColor(IdentifiableSupplier<? extends Color> gridColor) {
      attribute(PREFIX + "gridColor", JTable::getGridColor, JTable::setGridColor, gridColor);
    }

    public void intercellSpacing(IdentifiableSupplier<? extends Dimension> intercellSpacing) {
      attribute(PREFIX + "intercellSpacing", JTable::getIntercellSpacing, JTable::setIntercellSpacing, intercellSpacing);
    }

    public void model(IdentifiableSupplier<? extends TableModel> model) {
      attribute(PREFIX + "model", JTable::getModel, JTable::setModel, model);
    }

    public void preferredScrollableViewportSize(
        IdentifiableSupplier<? extends Dimension> preferredScrollableViewportSize) {
      attribute(PREFIX + "preferredScrollableViewportSize", JTable::getPreferredScrollableViewportSize, JTable::setPreferredScrollableViewportSize, preferredScrollableViewportSize);
    }

    public void rowHeight(IdentifiableSupplier<Integer> rowHeight) {
      attribute(PREFIX + "rowHeight", JTable::getRowHeight, JTable::setRowHeight, rowHeight);
    }

    public void rowMargin(IdentifiableSupplier<Integer> rowMargin) {
      attribute(PREFIX + "rowMargin", JTable::getRowMargin, JTable::setRowMargin, rowMargin);
    }

    public void rowSelectionAllowed(IdentifiableSupplier<Boolean> rowSelectionAllowed) {
      attribute(PREFIX + "rowSelectionAllowed", JTable::getRowSelectionAllowed, JTable::setRowSelectionAllowed, rowSelectionAllowed);
    }

    public void rowSorter(IdentifiableSupplier<? extends RowSorter<? extends TableModel>> rowSorter) {
      attribute(PREFIX + "rowSorter", JTable::getRowSorter, JTable::setRowSorter, rowSorter);
    }

    public void selectionBackground(IdentifiableSupplier<? extends Color> selectionBackground) {
      attribute(PREFIX + "selectionBackground", JTable::getSelectionBackground, JTable::setSelectionBackground, selectionBackground);
    }

    public void selectionForeground(IdentifiableSupplier<? extends Color> selectionForeground) {
      attribute(PREFIX + "selectionForeground", JTable::getSelectionForeground, JTable::setSelectionForeground, selectionForeground);
    }

    public void selectionMode(IdentifiableSupplier<Integer> selectionMode) {
      attribute(PREFIX + "selectionMode", t -> t.getSelectionModel().getSelectionMode(), JTable::setSelectionMode, selectionMode);
    }

    public void selectionModel(IdentifiableSupplier<? extends ListSelectionModel> selectionModel) {
      attribute(PREFIX + "selectionModel", JTable::getSelectionModel, JTable::setSelectionModel, selectionModel);
    }

    public void showGrid(IdentifiableSupplier<Boolean> showGrid) {
      attribute(PREFIX + "showGrid", JTable::setShowGrid, showGrid);
    }

    public void showHorizontalLines(IdentifiableSupplier<Boolean> showHorizontalLines) {
      attribute(PREFIX + "showHorizontalLines", JTable::getShowHorizontalLines, JTable::setShowHorizontalLines, showHorizontalLines);
    }

    public void showVerticalLines(IdentifiableSupplier<Boolean> showVerticalLines) {
      attribute(PREFIX + "showVerticalLines", JTable::getShowVerticalLines, JTable::setShowVerticalLines, showVerticalLines);
    }

    public void surrendersFocusOnKeystroke(IdentifiableSupplier<Boolean> surrendersFocusOnKeystroke) {
      attribute(PREFIX + "surrendersFocusOnKeystroke", JTable::getSurrendersFocusOnKeystroke, JTable::setSurrendersFocusOnKeystroke, surrendersFocusOnKeystroke);
    }

    public void tableHeader(
        @Nullable DeclarativeComponentSupplier<? extends JTableHeader> tableHeader) {
      fnAttribute(PREFIX + "tableHeader", JTable::getTableHeader, JTable::setTableHeader, tableHeader);
    }

    public void updateSelectionOnSort(IdentifiableSupplier<Boolean> updateSelectionOnSort) {
      attribute(PREFIX + "updateSelectionOnSort", JTable::getUpdateSelectionOnSort, JTable::setUpdateSelectionOnSort, updateSelectionOnSort);
    }
  }
}
