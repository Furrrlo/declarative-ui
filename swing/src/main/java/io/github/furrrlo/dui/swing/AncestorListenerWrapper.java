package io.github.furrrlo.dui.swing;

import org.jspecify.annotations.Nullable;

import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

class AncestorListenerWrapper implements EventListenerWrapper<AncestorListener>, AncestorListener {

    private @Nullable AncestorListener wrapped;

    public AncestorListenerWrapper(@Nullable AncestorListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void setWrapped(@Nullable AncestorListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void ancestorAdded(AncestorEvent event) {
        if(wrapped != null)
            wrapped.ancestorAdded(event);
    }

    @Override
    public void ancestorRemoved(AncestorEvent event) {
        if(wrapped != null)
            wrapped.ancestorRemoved(event);
    }

    @Override
    public void ancestorMoved(AncestorEvent event) {
        if(wrapped != null)
            wrapped.ancestorMoved(event);
    }
}
