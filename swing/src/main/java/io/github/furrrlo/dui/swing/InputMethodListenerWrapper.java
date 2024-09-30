package io.github.furrrlo.dui.swing;

import org.jspecify.annotations.Nullable;

import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;

class InputMethodListenerWrapper implements EventListenerWrapper<InputMethodListener>, InputMethodListener {

    private @Nullable InputMethodListener wrapped;

    public InputMethodListenerWrapper(@Nullable InputMethodListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void setWrapped(@Nullable InputMethodListener wrapped) {
        this.wrapped = wrapped;
    }


    @Override
    public void inputMethodTextChanged(InputMethodEvent event) {
        if(wrapped != null)
            wrapped.inputMethodTextChanged(event);
    }

    @Override
    public void caretPositionChanged(InputMethodEvent event) {
        if(wrapped != null)
            wrapped.caretPositionChanged(event);
    }
}
