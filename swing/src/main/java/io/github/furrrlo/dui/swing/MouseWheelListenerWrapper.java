package io.github.furrrlo.dui.swing;

import org.jspecify.annotations.Nullable;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

class MouseWheelListenerWrapper implements EventListenerWrapper<MouseWheelListener>, MouseWheelListener {

    private @Nullable MouseWheelListener wrapped;

    public MouseWheelListenerWrapper(@Nullable MouseWheelListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void setWrapped(@Nullable MouseWheelListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if(wrapped != null)
            wrapped.mouseWheelMoved(e);
    }
}
