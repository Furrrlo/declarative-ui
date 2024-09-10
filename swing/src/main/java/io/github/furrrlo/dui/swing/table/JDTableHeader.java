package io.github.furrrlo.dui.swing.table;

import io.github.furrrlo.dui.*;
import io.github.furrrlo.dui.swing.JDComponent;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.plaf.TableHeaderUI;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDTableHeader {
  public static DeclarativeComponent<JTableHeader> fn(IdentityFreeConsumer<Decorator<JTableHeader>> body) {
    return fn(JTableHeader.class, JTableHeader::new, body);
  }

  public static DeclarativeComponent<JTableHeader> fn(Supplier<JTableHeader> factory,
      IdentityFreeConsumer<Decorator<JTableHeader>> body) {
    return fn(JTableHeader.class, factory, body);
  }

  public static <T extends JTableHeader> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentityFreeConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JTableHeader> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__JDTableHeader__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void ui(IdentityFreeSupplier<? extends TableHeaderUI> ui) {
      attribute(PREFIX + "ui", JTableHeader::getUI, JTableHeader::setUI, ui);
    }

    public void columnModel(IdentityFreeSupplier<? extends TableColumnModel> columnModel) {
      attribute(PREFIX + "columnModel", JTableHeader::getColumnModel, JTableHeader::setColumnModel, columnModel);
    }

    public void defaultRenderer(IdentityFreeSupplier<? extends TableCellRenderer> defaultRenderer) {
      attribute(PREFIX + "defaultRenderer", JTableHeader::getDefaultRenderer, JTableHeader::setDefaultRenderer, defaultRenderer);
    }

    public void draggedColumn(IdentityFreeSupplier<? extends TableColumn> draggedColumn) {
      attribute(PREFIX + "draggedColumn", JTableHeader::getDraggedColumn, JTableHeader::setDraggedColumn, draggedColumn);
    }

    public void draggedDistance(IdentityFreeSupplier<Integer> draggedDistance) {
      attribute(PREFIX + "draggedDistance", JTableHeader::getDraggedDistance, JTableHeader::setDraggedDistance, draggedDistance);
    }

    public void reorderingAllowed(IdentityFreeSupplier<Boolean> reorderingAllowed) {
      attribute(PREFIX + "reorderingAllowed", JTableHeader::getReorderingAllowed, JTableHeader::setReorderingAllowed, reorderingAllowed);
    }

    public void resizingAllowed(IdentityFreeSupplier<Boolean> resizingAllowed) {
      attribute(PREFIX + "resizingAllowed", JTableHeader::getResizingAllowed, JTableHeader::setResizingAllowed, resizingAllowed);
    }

    public void resizingColumn(IdentityFreeSupplier<? extends TableColumn> resizingColumn) {
      attribute(PREFIX + "resizingColumn", JTableHeader::getResizingColumn, JTableHeader::setResizingColumn, resizingColumn);
    }

    public void table(@Nullable DeclarativeComponentSupplier<? extends JTable> table) {
      fnAttribute(PREFIX + "table", JTableHeader::getTable, JTableHeader::setTable, table);
    }

    public void updateTableInRealTime(IdentityFreeSupplier<Boolean> updateTableInRealTime) {
      attribute(PREFIX + "updateTableInRealTime", JTableHeader::getUpdateTableInRealTime, JTableHeader::setUpdateTableInRealTime, updateTableInRealTime);
    }
  }
}
