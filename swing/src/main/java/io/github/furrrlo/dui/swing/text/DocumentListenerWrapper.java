package io.github.furrrlo.dui.swing.text;

import io.github.furrrlo.dui.swing.EventListenerWrapper;
import org.jspecify.annotations.Nullable;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

class DocumentListenerWrapper implements DocumentListener, EventListenerWrapper<DocumentListener> {

    private @Nullable DocumentListener wrapped;

    public DocumentListenerWrapper(@Nullable DocumentListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void setWrapped(@Nullable DocumentListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        if(wrapped != null)
            wrapped.insertUpdate(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        if(wrapped != null)
            wrapped.removeUpdate(e);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        if(wrapped != null)
            wrapped.changedUpdate(e);
    }
}
