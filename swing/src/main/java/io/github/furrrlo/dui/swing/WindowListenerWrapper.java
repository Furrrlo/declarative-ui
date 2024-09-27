package io.github.furrrlo.dui.swing;

import org.jspecify.annotations.Nullable;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

class WindowListenerWrapper implements WindowListener, EventListenerWrapper<WindowListener> {

    private @Nullable WindowListener wrapped;

    public WindowListenerWrapper(@Nullable WindowListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void setWrapped(@Nullable WindowListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void windowOpened(WindowEvent e) {
        if (wrapped != null)
            wrapped.windowOpened(e);
    }

    @Override
    public void windowClosing(WindowEvent e) {
        if (wrapped != null)
            wrapped.windowClosing(e);
    }

    @Override
    public void windowClosed(WindowEvent e) {
        if (wrapped != null)
            wrapped.windowClosed(e);
    }

    @Override
    public void windowIconified(WindowEvent e) {
        if (wrapped != null)
            wrapped.windowIconified(e);
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        if (wrapped != null)
            wrapped.windowDeiconified(e);
    }

    @Override
    public void windowActivated(WindowEvent e) {
        if (wrapped != null)
            wrapped.windowActivated(e);
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        if (wrapped != null)
            wrapped.windowDeactivated(e);
    }
}
