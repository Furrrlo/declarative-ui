package io.github.furrrlo.dui.swing;

import org.jspecify.annotations.Nullable;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

class WindowFocusListenerWrapper implements WindowFocusListener, EventListenerWrapper<WindowFocusListener> {

    private @Nullable WindowFocusListener wrapped;

    public WindowFocusListenerWrapper(@Nullable WindowFocusListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void setWrapped(@Nullable WindowFocusListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void windowGainedFocus(WindowEvent e) {
        if(wrapped != null)
            wrapped.windowGainedFocus(e);
    }

    @Override
    public void windowLostFocus(WindowEvent e) {
        if(wrapped != null)
            wrapped.windowLostFocus(e);
    }
}
