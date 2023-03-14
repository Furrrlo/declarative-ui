package io.github.furrrlo.dui.swing;

import org.jetbrains.annotations.Nullable;

import java.util.EventListener;

public interface EventListenerWrapper<L extends EventListener> {

    void setWrapped(@Nullable L wrapped);
}
