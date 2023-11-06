package io.github.furrrlo.dui.swing.table;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.DeclarativeComponentSupplier;
import io.github.furrrlo.dui.IdentifiableConsumer;
import io.github.furrrlo.dui.swing.JDComponent;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.Integer;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.function.Supplier;
import javax.swing.JTable;
import javax.swing.plaf.TableHeaderUI;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class JDTableHeader {
  public static DeclarativeComponent<JTableHeader> fn(IdentifiableConsumer<Decorator<JTableHeader>> body) {
    return fn(JTableHeader.class, JTableHeader::new, body);
  }

  public static DeclarativeComponent<JTableHeader> fn(Supplier<JTableHeader> factory,
      IdentifiableConsumer<Decorator<JTableHeader>> body) {
    return fn(JTableHeader.class, factory, body);
  }

  public static <T extends JTableHeader> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentifiableConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JTableHeader> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__JDTableHeader__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void ui(Supplier<? extends TableHeaderUI> ui) {
      attribute(PREFIX + "ui", JTableHeader::getUI, JTableHeader::setUI, ui);
    }

    public void columnModel(Supplier<? extends TableColumnModel> columnModel) {
      attribute(PREFIX + "columnModel", JTableHeader::getColumnModel, JTableHeader::setColumnModel, columnModel);
    }

    public void defaultRenderer(Supplier<? extends TableCellRenderer> defaultRenderer) {
      attribute(PREFIX + "defaultRenderer", JTableHeader::getDefaultRenderer, JTableHeader::setDefaultRenderer, defaultRenderer);
    }

    public void draggedColumn(Supplier<? extends TableColumn> draggedColumn) {
      attribute(PREFIX + "draggedColumn", JTableHeader::getDraggedColumn, JTableHeader::setDraggedColumn, draggedColumn);
    }

    public void draggedDistance(Supplier<Integer> draggedDistance) {
      attribute(PREFIX + "draggedDistance", JTableHeader::getDraggedDistance, JTableHeader::setDraggedDistance, draggedDistance);
    }

    public void reorderingAllowed(Supplier<Boolean> reorderingAllowed) {
      attribute(PREFIX + "reorderingAllowed", JTableHeader::getReorderingAllowed, JTableHeader::setReorderingAllowed, reorderingAllowed);
    }

    public void resizingAllowed(Supplier<Boolean> resizingAllowed) {
      attribute(PREFIX + "resizingAllowed", JTableHeader::getResizingAllowed, JTableHeader::setResizingAllowed, resizingAllowed);
    }

    public void resizingColumn(Supplier<? extends TableColumn> resizingColumn) {
      attribute(PREFIX + "resizingColumn", JTableHeader::getResizingColumn, JTableHeader::setResizingColumn, resizingColumn);
    }

    public void table(@Nullable DeclarativeComponentSupplier<? extends JTable> table) {
      fnAttribute(PREFIX + "table", JTableHeader::getTable, JTableHeader::setTable, table);
    }

    public void updateTableInRealTime(Supplier<Boolean> updateTableInRealTime) {
      attribute(PREFIX + "updateTableInRealTime", JTableHeader::getUpdateTableInRealTime, JTableHeader::setUpdateTableInRealTime, updateTableInRealTime);
    }
  }
}
