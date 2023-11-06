package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;

import javax.swing.*;
import java.util.function.Supplier;

public class JDScrollPane {

    public static DeclarativeComponent<JScrollPane> fn(IdentifiableConsumer<Decorator<JScrollPane>> body) {
        return fn(JScrollPane.class, JScrollPane::new, body);
    }

    public static DeclarativeComponent<JScrollPane> fn(Supplier<JScrollPane> factory,
                                                       IdentifiableConsumer<Decorator<JScrollPane>> body) {
        return fn(JScrollPane.class, factory, body);
    }

    public static <T extends JScrollPane> DeclarativeComponent<T> fn(Class<T> type,
                                                                     Supplier<T> factory,
                                                                     IdentifiableConsumer<Decorator<T>> body) {
        return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
    }

    public static class Decorator<T extends JScrollPane> extends JDComponent.Decorator<T> {

        private static final String PREFIX = "__JDScrollPane__";

        protected Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);
        }
    
public void ui(java.util.function.Supplier<? extends javax.swing.plaf.ScrollPaneUI> ui) {
  attribute(PREFIX + "ui", javax.swing.JScrollPane::getUI, javax.swing.JScrollPane::setUI, ui);
}

public void columnHeader(
    io.github.furrrlo.dui. @org.jetbrains.annotations.Nullable DeclarativeComponentSupplier<? extends javax.swing.JViewport> columnHeader) {
  fnAttribute(PREFIX + "columnHeader", javax.swing.JScrollPane::getColumnHeader, javax.swing.JScrollPane::setColumnHeader, columnHeader);
}

public void columnHeaderView(
    io.github.furrrlo.dui. @org.jetbrains.annotations.Nullable DeclarativeComponentSupplier<? extends java.awt.Component> columnHeaderView) {
  fnAttribute(PREFIX + "columnHeaderView", javax.swing.JScrollPane::setColumnHeaderView, columnHeaderView);
}

public void componentOrientation(
    java.util.function.Supplier<java.awt.ComponentOrientation> componentOrientation) {
  attribute(PREFIX + "componentOrientation", javax.swing.JScrollPane::setComponentOrientation, componentOrientation);
}

public void horizontalScrollBar(
    io.github.furrrlo.dui. @org.jetbrains.annotations.Nullable DeclarativeComponentSupplier<? extends javax.swing.JScrollBar> horizontalScrollBar) {
  fnAttribute(PREFIX + "horizontalScrollBar", javax.swing.JScrollPane::getHorizontalScrollBar, javax.swing.JScrollPane::setHorizontalScrollBar, horizontalScrollBar);
}

public void horizontalScrollBarPolicy(
    java.util.function.Supplier<java.lang.Integer> horizontalScrollBarPolicy) {
  attribute(PREFIX + "horizontalScrollBarPolicy", javax.swing.JScrollPane::getHorizontalScrollBarPolicy, javax.swing.JScrollPane::setHorizontalScrollBarPolicy, horizontalScrollBarPolicy);
}

public void layout(java.util.function.Supplier<? extends java.awt.LayoutManager> layout) {
  attribute(PREFIX + "layout", javax.swing.JScrollPane::setLayout, layout);
}

public void rowHeader(
    io.github.furrrlo.dui. @org.jetbrains.annotations.Nullable DeclarativeComponentSupplier<? extends javax.swing.JViewport> rowHeader) {
  fnAttribute(PREFIX + "rowHeader", javax.swing.JScrollPane::getRowHeader, javax.swing.JScrollPane::setRowHeader, rowHeader);
}

public void rowHeaderView(
    io.github.furrrlo.dui. @org.jetbrains.annotations.Nullable DeclarativeComponentSupplier<? extends java.awt.Component> rowHeaderView) {
  fnAttribute(PREFIX + "rowHeaderView", javax.swing.JScrollPane::setRowHeaderView, rowHeaderView);
}

public void verticalScrollBar(
    io.github.furrrlo.dui. @org.jetbrains.annotations.Nullable DeclarativeComponentSupplier<? extends javax.swing.JScrollBar> verticalScrollBar) {
  fnAttribute(PREFIX + "verticalScrollBar", javax.swing.JScrollPane::getVerticalScrollBar, javax.swing.JScrollPane::setVerticalScrollBar, verticalScrollBar);
}

public void verticalScrollBarPolicy(
    java.util.function.Supplier<java.lang.Integer> verticalScrollBarPolicy) {
  attribute(PREFIX + "verticalScrollBarPolicy", javax.swing.JScrollPane::getVerticalScrollBarPolicy, javax.swing.JScrollPane::setVerticalScrollBarPolicy, verticalScrollBarPolicy);
}

public void viewport(
    io.github.furrrlo.dui. @org.jetbrains.annotations.Nullable DeclarativeComponentSupplier<? extends javax.swing.JViewport> viewport) {
  fnAttribute(PREFIX + "viewport", javax.swing.JScrollPane::getViewport, javax.swing.JScrollPane::setViewport, viewport);
}

public void viewportBorder(
    java.util.function.Supplier<? extends javax.swing.border.Border> viewportBorder) {
  attribute(PREFIX + "viewportBorder", javax.swing.JScrollPane::getViewportBorder, javax.swing.JScrollPane::setViewportBorder, viewportBorder);
}

public void viewportView(
    io.github.furrrlo.dui. @org.jetbrains.annotations.Nullable DeclarativeComponentSupplier<? extends java.awt.Component> viewportView) {
  fnAttribute(PREFIX + "viewportView", javax.swing.JScrollPane::setViewportView, viewportView);
}

public void wheelScrollingEnabled(
    java.util.function.Supplier<java.lang.Boolean> wheelScrollingEnabled) {
  attribute(PREFIX + "wheelScrollingEnabled", javax.swing.JScrollPane::isWheelScrollingEnabled, javax.swing.JScrollPane::setWheelScrollingEnabled, wheelScrollingEnabled);
}
}
}
