package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.*;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
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

    public static class Decorator<T extends JFrame> extends SwingDecorator<T> {

        private static final String PREFIX = "__JDFrame__";

        protected Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);

            setDisposer(window -> {
                window.setVisible(false);
                window.dispose();
            });
        }

        public void visible(IdentityFreeSupplier<Boolean> visible) {
            attribute(PREFIX + "visible", Frame::isVisible, Frame::setVisible, visible);
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


        public void minimumSize(IdentityFreeSupplier<? extends Dimension> dimension) {
            attribute(PREFIX + "minimumSize", JFrame::getMinimumSize, JFrame::setMinimumSize, dimension);
        }


        public void size(IdentityFreeSupplier<? extends Dimension> dimension) {
            attribute(PREFIX + "size", JFrame::getSize, JFrame::setSize, dimension);
        }

        public void locationRelativeTo(IdentityFreeSupplier<Component> c) {
            attribute(PREFIX + "locationRelativeTo", JFrame::setLocationRelativeTo, c);
        }
    }
}
