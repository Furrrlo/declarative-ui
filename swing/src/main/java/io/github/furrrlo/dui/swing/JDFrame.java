package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.*;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

public class JDFrame {

    public static DeclarativeComponent<JFrame> fn(DeclarativeComponent.Body<JFrame, Decorator<JFrame>> body) {
        return fn(JFrame.class, JFrame::new, body);
    }

    public static DeclarativeComponent<JFrame> fn(Supplier<JFrame> factory,
                                                  DeclarativeComponent.Body<JFrame, Decorator<JFrame>> body) {
        return fn(JFrame.class, factory, body);
    }

    public static <T extends JFrame> DeclarativeComponent<T> fn(Class<T> type,
                                                                Supplier<T> factory,
                                                                DeclarativeComponent.Body<T, Decorator<T>> body) {
        return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
    }

    public static class Decorator<T extends JFrame> extends SwingDecorator<T> {

        private static final String PREFIX = "__JDFrame__";

        protected Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);
        }

        public void visible(Supplier<Boolean> visible) {
            attribute(PREFIX + "visible", Frame::setVisible, visible);
        }

        public void title(Supplier<String> title) {
            attribute(PREFIX + "title", Frame::setTitle, title);
        }

        public void defaultCloseOperation(Supplier<Integer> operation) {
            attribute(PREFIX + "defaultCloseOperation", JFrame::setDefaultCloseOperation, operation);
        }

        public <C extends Container> void contentPane(@Nullable DeclarativeComponentSupplier<C> contentPane) {
            fnAttribute(PREFIX + "contentPane", JFrame::setContentPane, contentPane != null ?
                    contentPane.doApply() :
                    DNull.nullFn());
        }


        public void minimumSize(Supplier<Dimension> dimension) {
            attribute(PREFIX + "minimumSize", JFrame::setMinimumSize, dimension);
        }


        public void size(Supplier<Dimension> dimension) {
            attribute(PREFIX + "size", JFrame::setSize, dimension);
        }

        public void locationRelativeTo(Supplier<Component> c) {
            attribute(PREFIX + "locationRelativeTo", JFrame::setLocationRelativeTo, c);
        }
    }
}
