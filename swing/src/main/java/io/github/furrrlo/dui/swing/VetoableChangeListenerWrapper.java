package io.github.furrrlo.dui.swing;

import org.jspecify.annotations.Nullable;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;

class VetoableChangeListenerWrapper implements EventListenerWrapper<VetoableChangeListener>, VetoableChangeListener {

    private @Nullable VetoableChangeListener wrapped;

    public VetoableChangeListenerWrapper(@Nullable VetoableChangeListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void setWrapped(@Nullable VetoableChangeListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
        if(wrapped != null)
            wrapped.vetoableChange(evt);
    }
}
