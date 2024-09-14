package io.github.furrrlo.dui.swing;

import org.jspecify.annotations.Nullable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ActionListenerWrapper implements ActionListener, EventListenerWrapper<ActionListener> {

    private @Nullable ActionListener wrapped;

    public ActionListenerWrapper(@Nullable ActionListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void setWrapped(@Nullable ActionListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (wrapped != null)
            wrapped.actionPerformed(e);
    }
}
