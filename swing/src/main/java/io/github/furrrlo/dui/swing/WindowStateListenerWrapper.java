package io.github.furrrlo.dui.swing;

import org.jspecify.annotations.Nullable;

import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;

class WindowStateListenerWrapper implements WindowStateListener, EventListenerWrapper<WindowStateListener> {

    private @Nullable WindowStateListener wrapped;

    public WindowStateListenerWrapper(@Nullable WindowStateListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void setWrapped(@Nullable WindowStateListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void windowStateChanged(WindowEvent e) {
        if(wrapped != null)
            wrapped.windowStateChanged(e);
    }
}
