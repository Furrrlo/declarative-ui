package io.github.furrrlo.dui.swing;

import org.jspecify.annotations.Nullable;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

class PropertyChangeListenerWrapper implements EventListenerWrapper<PropertyChangeListener>, PropertyChangeListener {

    private @Nullable PropertyChangeListener wrapped;

    public PropertyChangeListenerWrapper(@Nullable PropertyChangeListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void setWrapped(@Nullable PropertyChangeListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(wrapped != null)
            wrapped.propertyChange(evt);
    }
}
