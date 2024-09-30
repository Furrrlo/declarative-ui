package io.github.furrrlo.dui.swing;

import org.jspecify.annotations.Nullable;

import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;

class ContainerListenerWrapper implements EventListenerWrapper<ContainerListener>, ContainerListener {

    private @Nullable ContainerListener wrapped;

    public ContainerListenerWrapper(@Nullable ContainerListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void setWrapped(@Nullable ContainerListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void componentAdded(ContainerEvent e) {
        if(wrapped != null)
            wrapped.componentAdded(e);
    }

    @Override
    public void componentRemoved(ContainerEvent e) {
        if(wrapped != null)
            wrapped.componentRemoved(e);
    }
}
