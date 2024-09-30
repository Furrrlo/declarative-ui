package io.github.furrrlo.dui.swing;

import org.jspecify.annotations.Nullable;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

class KeyListenerWrapper implements EventListenerWrapper<KeyListener>, KeyListener {

    private @Nullable KeyListener wrapped;

    public KeyListenerWrapper(@Nullable KeyListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void setWrapped(@Nullable KeyListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if(wrapped != null)
            wrapped.keyTyped(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(wrapped != null)
            wrapped.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(wrapped != null)
            wrapped.keyReleased(e);
    }
}
