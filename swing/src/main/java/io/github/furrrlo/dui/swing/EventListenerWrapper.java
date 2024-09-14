package io.github.furrrlo.dui.swing;

import org.jspecify.annotations.Nullable;

import java.util.EventListener;

public interface EventListenerWrapper<L extends EventListener> {

    void setWrapped(@Nullable L wrapped);
}
