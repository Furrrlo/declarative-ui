package io.github.furrrlo.dui.swing;

import org.jspecify.annotations.Nullable;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

class FocusListenerWrapper implements EventListenerWrapper<FocusListener>, FocusListener {

    private @Nullable FocusListener wrapped;

    public FocusListenerWrapper(@Nullable FocusListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void setWrapped(@Nullable FocusListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void focusGained(FocusEvent e) {
        if(wrapped != null)
            wrapped.focusGained(e);
    }

    @Override
    public void focusLost(FocusEvent e) {
        if(wrapped != null)
            wrapped.focusLost(e);
    }
}
