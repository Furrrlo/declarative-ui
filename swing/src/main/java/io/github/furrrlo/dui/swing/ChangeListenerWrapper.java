package io.github.furrrlo.dui.swing;

import org.jetbrains.annotations.Nullable;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class ChangeListenerWrapper implements ChangeListener, EventListenerWrapper<ChangeListener> {

    private @Nullable ChangeListener wrapped;

    public ChangeListenerWrapper(@Nullable ChangeListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void setWrapped(@Nullable ChangeListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (wrapped != null)
            wrapped.stateChanged(e);
    }
}
