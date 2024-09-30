package io.github.furrrlo.dui.swing;

import org.jspecify.annotations.Nullable;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

class ComponentListenerWrapper implements EventListenerWrapper<ComponentListener>, ComponentListener {

    private @Nullable ComponentListener wrapped;

    public ComponentListenerWrapper(@Nullable ComponentListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void setWrapped(@Nullable ComponentListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        if(wrapped != null)
            wrapped.componentResized(e);
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        if(wrapped != null)
            wrapped.componentMoved(e);
    }

    @Override
    public void componentShown(ComponentEvent e) {
        if(wrapped != null)
            wrapped.componentShown(e);
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        if(wrapped != null)
            wrapped.componentHidden(e);
    }
}
