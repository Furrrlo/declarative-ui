package io.github.furrrlo.dui.swing;

import org.jspecify.annotations.Nullable;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

class MouseListenerWrapper implements EventListenerWrapper<MouseListener>, MouseListener {

    private @Nullable MouseListener wrapped;

    public MouseListenerWrapper(@Nullable MouseListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void setWrapped(@Nullable MouseListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(wrapped != null)
            wrapped.mouseClicked(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(wrapped != null)
            wrapped.mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(wrapped != null)
            wrapped.mouseReleased(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if(wrapped != null)
            wrapped.mouseEntered(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if(wrapped != null)
            wrapped.mouseExited(e);
    }
}
