package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.*;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ScrollPaneUI;
import java.awt.*;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDScrollPane {

    public static DeclarativeComponent<JScrollPane> fn(IdentityFreeConsumer<Decorator<JScrollPane>> body) {
        return fn(JScrollPane.class, JScrollPane::new, body);
    }

    public static DeclarativeComponent<JScrollPane> fn(Supplier<JScrollPane> factory,
                                                       IdentityFreeConsumer<Decorator<JScrollPane>> body) {
        return fn(JScrollPane.class, factory, body);
    }

    public static <T extends JScrollPane> DeclarativeComponent<T> fn(Class<T> type,
                                                                     Supplier<T> factory,
                                                                     IdentityFreeConsumer<Decorator<T>> body) {
        return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
    }

    public static class Decorator<T extends JScrollPane> extends JDComponent.Decorator<T> {

        private static final String PREFIX = "__JDScrollPane__";

        protected Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);
        }

        public void ui(IdentityFreeSupplier<? extends ScrollPaneUI> ui) {
            attribute(PREFIX + "ui", JScrollPane::getUI, JScrollPane::setUI, ui);
        }

        public void columnHeader(@Nullable DeclarativeComponentSupplier<? extends JViewport> columnHeader) {
            fnAttribute(PREFIX + "columnHeader", JScrollPane::getColumnHeader, JScrollPane::setColumnHeader, columnHeader);
        }

        public void columnHeaderView(@Nullable DeclarativeComponentSupplier<? extends Component> columnHeaderView) {
            fnAttribute(PREFIX + "columnHeaderView", JScrollPane::setColumnHeaderView, columnHeaderView);
        }

        public void horizontalScrollBar(@Nullable DeclarativeComponentSupplier<? extends JScrollBar> horizontalScrollBar) {
            fnAttribute(PREFIX + "horizontalScrollBar", JScrollPane::getHorizontalScrollBar, JScrollPane::setHorizontalScrollBar, horizontalScrollBar);
        }

        public void horizontalScrollBarPolicy(IdentityFreeSupplier<Integer> horizontalScrollBarPolicy) {
            attribute(PREFIX + "horizontalScrollBarPolicy", JScrollPane::getHorizontalScrollBarPolicy, JScrollPane::setHorizontalScrollBarPolicy, horizontalScrollBarPolicy);
        }

        public void rowHeader(@Nullable DeclarativeComponentSupplier<? extends JViewport> rowHeader) {
            fnAttribute(PREFIX + "rowHeader", JScrollPane::getRowHeader, JScrollPane::setRowHeader, rowHeader);
        }

        public void rowHeaderView(@Nullable DeclarativeComponentSupplier<? extends Component> rowHeaderView) {
            fnAttribute(PREFIX + "rowHeaderView", JScrollPane::setRowHeaderView, rowHeaderView);
        }

        public void verticalScrollBar(@Nullable DeclarativeComponentSupplier<? extends JScrollBar> verticalScrollBar) {
            fnAttribute(PREFIX + "verticalScrollBar", JScrollPane::getVerticalScrollBar, JScrollPane::setVerticalScrollBar, verticalScrollBar);
        }

        public void verticalScrollBarPolicy(IdentityFreeSupplier<Integer> verticalScrollBarPolicy) {
            attribute(PREFIX + "verticalScrollBarPolicy", JScrollPane::getVerticalScrollBarPolicy, JScrollPane::setVerticalScrollBarPolicy, verticalScrollBarPolicy);
        }

        public void viewport(@Nullable DeclarativeComponentSupplier<? extends JViewport> viewport) {
            fnAttribute(PREFIX + "viewport", JScrollPane::getViewport, JScrollPane::setViewport, viewport);
        }

        public void viewportBorder(IdentityFreeSupplier<? extends Border> viewportBorder) {
            attribute(PREFIX + "viewportBorder", JScrollPane::getViewportBorder, JScrollPane::setViewportBorder, viewportBorder);
        }

        public void viewportView(@Nullable DeclarativeComponentSupplier<? extends Component> viewportView) {
            fnAttribute(PREFIX + "viewportView", JScrollPane::setViewportView, viewportView);
        }

        public void wheelScrollingEnabled(IdentityFreeSupplier<Boolean> wheelScrollingEnabled) {
            attribute(PREFIX + "wheelScrollingEnabled", JScrollPane::isWheelScrollingEnabled, JScrollPane::setWheelScrollingEnabled, wheelScrollingEnabled);
        }
    }
}
