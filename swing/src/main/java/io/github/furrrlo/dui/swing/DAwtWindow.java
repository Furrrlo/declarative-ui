package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.IdentityFreeSupplier;
import io.leangen.geantyref.TypeToken;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.util.function.Supplier;

public class DAwtWindow {
    public static class Decorator<T extends Window> extends DAwtContainer.Decorator<T> {

        private static final String PREFIX = "__DAwtWindow__";

        protected Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);

            setDisposer(window -> {
                window.setVisible(false);
                window.dispose();
            });
        }

        protected Decorator(TypeToken<T> type, Supplier<T> factory) {
            super(type, factory);

            setDisposer(window -> {
                window.setVisible(false);
                window.dispose();
            });
        }

        public void locationRelativeTo(IdentityFreeSupplier<Component> c) {
            attribute(PREFIX + "locationRelativeTo", Window::setLocationRelativeTo, c);
        }

        public void isAlwaysOnTop(IdentityFreeSupplier<Boolean> visible) {
            attribute(PREFIX + "alwaysOnTop", Window::isAlwaysOnTop, Window::setAlwaysOnTop, visible);
        }

        public void windowListener(WindowListener l) {
            eventListener(PREFIX + "windowListener",
                    WindowListener.class,
                    WindowListenerWrapper::new,
                    Window::addWindowListener,
                    l);
        }

        public void windowFocusListener(WindowFocusListener l) {
            eventListener(PREFIX + "windowFocusListener",
                    WindowFocusListener.class,
                    WindowFocusListenerWrapper::new,
                    Window::addWindowFocusListener,
                    l);
        }

        public void windowStateListener(WindowStateListener l) {
            eventListener(PREFIX + "windowStateListener",
                    WindowStateListener.class,
                    WindowStateListenerWrapper::new,
                    Window::addWindowStateListener,
                    l);
        }
    }
}
