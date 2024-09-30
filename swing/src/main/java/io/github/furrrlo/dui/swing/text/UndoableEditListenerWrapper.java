package io.github.furrrlo.dui.swing.text;

import io.github.furrrlo.dui.swing.EventListenerWrapper;
import org.jspecify.annotations.Nullable;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

class UndoableEditListenerWrapper implements UndoableEditListener, EventListenerWrapper<UndoableEditListener> {

    private @Nullable UndoableEditListener wrapped;

    public UndoableEditListenerWrapper(@Nullable UndoableEditListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void setWrapped(@Nullable UndoableEditListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void undoableEditHappened(UndoableEditEvent e) {
        if(wrapped != null)
            wrapped.undoableEditHappened(e);
    }
}
