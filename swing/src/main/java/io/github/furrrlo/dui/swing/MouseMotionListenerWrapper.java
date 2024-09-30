package io.github.furrrlo.dui.swing;

import org.jspecify.annotations.Nullable;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

class MouseMotionListenerWrapper implements EventListenerWrapper<MouseMotionListener>, MouseMotionListener {

    private @Nullable MouseMotionListener wrapped;

    public MouseMotionListenerWrapper(@Nullable MouseMotionListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void setWrapped(@Nullable MouseMotionListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(wrapped != null)
            wrapped.mouseDragged(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if(wrapped != null)
            wrapped.mouseMoved(e);
    }
}
