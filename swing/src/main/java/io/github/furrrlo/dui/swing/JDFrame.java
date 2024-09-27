package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.*;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.util.Arrays;
import java.util.EventListener;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class JDFrame {

    public static DeclarativeComponent<JFrame> fn(IdentityFreeConsumer<Decorator<JFrame>> body) {
        return fn(JFrame.class, JFrame::new, body);
    }

    public static DeclarativeComponent<JFrame> fn(Supplier<JFrame> factory,
                                                  IdentityFreeConsumer<Decorator<JFrame>> body) {
        return fn(JFrame.class, factory, body);
    }

    public static <T extends JFrame> DeclarativeComponent<T> fn(Class<T> type,
                                                                Supplier<T> factory,
                                                                IdentityFreeConsumer<Decorator<T>> body) {
        return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
    }

    public static class Decorator<T extends JFrame> extends DAwtWindow.Decorator<T> {

        private static final String PREFIX = "__JDFrame__";

        protected Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);
        }

        public void title(IdentityFreeSupplier<String> title) {
            attribute(PREFIX + "title", Frame::getTitle, Frame::setTitle, title);
        }

        public void defaultCloseOperation(IdentityFreeSupplier<Integer> operation) {
            attribute(PREFIX + "defaultCloseOperation", JFrame::getDefaultCloseOperation, JFrame::setDefaultCloseOperation, operation);
        }

        public void contentPane(@Nullable DeclarativeComponentSupplier<? extends Container> contentPane) {
            fnAttribute(PREFIX + "contentPane", JFrame::setContentPane, contentPane);
        }

        public void size(IdentityFreeSupplier<? extends Dimension> dimension) {
            attribute(PREFIX + "size", JFrame::getSize, JFrame::setSize, dimension);
        }

        public void jMenuBar(
                @Nullable DeclarativeComponentSupplier<? extends JMenuBar> JMenuBar) {
            fnAttribute(PREFIX + "JMenuBar", JFrame::getJMenuBar, JFrame::setJMenuBar, JMenuBar);
        }

        public void glassPane(
                @Nullable DeclarativeComponentSupplier<? extends Component> glassPane) {
            fnAttribute(PREFIX + "glassPane", JFrame::getGlassPane, JFrame::setGlassPane, glassPane);
        }

        public void layeredPane(
                @Nullable DeclarativeComponentSupplier<? extends JLayeredPane> layeredPane) {
            fnAttribute(PREFIX + "layeredPane", JFrame::getLayeredPane, JFrame::setLayeredPane, layeredPane);
        }

        public void transferHandler(
                IdentityFreeSupplier<? extends TransferHandler> transferHandler) {
            attribute(PREFIX + "transferHandler", JFrame::getTransferHandler, JFrame::setTransferHandler, transferHandler);
        }
    }
}
