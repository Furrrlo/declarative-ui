package io.github.furrrlo.dui.swing;

import org.jspecify.annotations.Nullable;

import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;

class HierarchyListenerWrapper implements EventListenerWrapper<HierarchyListener>, HierarchyListener {

    private @Nullable HierarchyListener wrapped;

    public HierarchyListenerWrapper(@Nullable HierarchyListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void setWrapped(@Nullable HierarchyListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void hierarchyChanged(HierarchyEvent e) {
        if(wrapped != null)
            wrapped.hierarchyChanged(e);
    }
}
