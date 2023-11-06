package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;

import javax.swing.*;
import javax.swing.plaf.TreeUI;
import javax.swing.tree.*;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDTree {
  public static DeclarativeComponent<JTree> fn(IdentifiableConsumer<Decorator<JTree>> body) {
    return fn(JTree.class, JTree::new, body);
  }

  public static DeclarativeComponent<JTree> fn(Supplier<JTree> factory,
      IdentifiableConsumer<Decorator<JTree>> body) {
    return fn(JTree.class, factory, body);
  }

  public static <T extends JTree> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentifiableConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JTree> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__JDTree__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void ui(Supplier<? extends TreeUI> ui) {
      attribute(PREFIX + "ui", JTree::getUI, JTree::setUI, ui);
    }

    public void anchorSelectionPath(Supplier<? extends TreePath> anchorSelectionPath) {
      attribute(PREFIX + "anchorSelectionPath", JTree::getAnchorSelectionPath, JTree::setAnchorSelectionPath, anchorSelectionPath);
    }

    public void cellEditor(Supplier<? extends TreeCellEditor> cellEditor) {
      attribute(PREFIX + "cellEditor", JTree::getCellEditor, JTree::setCellEditor, cellEditor);
    }

    public void cellRenderer(Supplier<? extends TreeCellRenderer> cellRenderer) {
      attribute(PREFIX + "cellRenderer", JTree::getCellRenderer, JTree::setCellRenderer, cellRenderer);
    }

    public void dragEnabled(Supplier<Boolean> dragEnabled) {
      attribute(PREFIX + "dragEnabled", JTree::getDragEnabled, JTree::setDragEnabled, dragEnabled);
    }

    public void dropMode(Supplier<DropMode> dropMode) {
      attribute(PREFIX + "dropMode", JTree::getDropMode, JTree::setDropMode, dropMode);
    }

    public void editable(Supplier<Boolean> editable) {
      attribute(PREFIX + "editable", JTree::isEditable, JTree::setEditable, editable);
    }

    public void expandsSelectedPaths(Supplier<Boolean> expandsSelectedPaths) {
      attribute(PREFIX + "expandsSelectedPaths", JTree::getExpandsSelectedPaths, JTree::setExpandsSelectedPaths, expandsSelectedPaths);
    }

    public void invokesStopCellEditing(Supplier<Boolean> invokesStopCellEditing) {
      attribute(PREFIX + "invokesStopCellEditing", JTree::getInvokesStopCellEditing, JTree::setInvokesStopCellEditing, invokesStopCellEditing);
    }

    public void largeModel(Supplier<Boolean> largeModel) {
      attribute(PREFIX + "largeModel", JTree::isLargeModel, JTree::setLargeModel, largeModel);
    }

    public void leadSelectionPath(Supplier<? extends TreePath> leadSelectionPath) {
      attribute(PREFIX + "leadSelectionPath", JTree::getLeadSelectionPath, JTree::setLeadSelectionPath, leadSelectionPath);
    }

    public void model(Supplier<? extends TreeModel> model) {
      attribute(PREFIX + "model", JTree::getModel, JTree::setModel, model);
    }

    public void rootVisible(Supplier<Boolean> rootVisible) {
      attribute(PREFIX + "rootVisible", JTree::isRootVisible, JTree::setRootVisible, rootVisible);
    }

    public void rowHeight(Supplier<Integer> rowHeight) {
      attribute(PREFIX + "rowHeight", JTree::getRowHeight, JTree::setRowHeight, rowHeight);
    }

    public void scrollsOnExpand(Supplier<Boolean> scrollsOnExpand) {
      attribute(PREFIX + "scrollsOnExpand", JTree::getScrollsOnExpand, JTree::setScrollsOnExpand, scrollsOnExpand);
    }

    public void selectionModel(Supplier<? extends TreeSelectionModel> selectionModel) {
      attribute(PREFIX + "selectionModel", JTree::getSelectionModel, JTree::setSelectionModel, selectionModel);
    }

    public void selectionPath(Supplier<? extends TreePath> selectionPath) {
      attribute(PREFIX + "selectionPath", JTree::getSelectionPath, JTree::setSelectionPath, selectionPath);
    }

    public void selectionPaths() {
      // TODO: implement "selectionPaths"
    }

    public void selectionRow(Supplier<Integer> selectionRow) {
      attribute(PREFIX + "selectionRow", JTree::setSelectionRow, selectionRow);
    }

    public void selectionRows() {
      // TODO: implement "selectionRows"
    }

    public void showsRootHandles(Supplier<Boolean> showsRootHandles) {
      attribute(PREFIX + "showsRootHandles", JTree::getShowsRootHandles, JTree::setShowsRootHandles, showsRootHandles);
    }

    public void toggleClickCount(Supplier<Integer> toggleClickCount) {
      attribute(PREFIX + "toggleClickCount", JTree::getToggleClickCount, JTree::setToggleClickCount, toggleClickCount);
    }

    public void visibleRowCount(Supplier<Integer> visibleRowCount) {
      attribute(PREFIX + "visibleRowCount", JTree::getVisibleRowCount, JTree::setVisibleRowCount, visibleRowCount);
    }
  }
}
