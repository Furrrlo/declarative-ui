package io.github.furrrlo.dui.swing;

import org.jspecify.annotations.Nullable;

import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;

class HierarchyBoundsListenerWrapper implements EventListenerWrapper<HierarchyBoundsListener>, HierarchyBoundsListener {

    private @Nullable HierarchyBoundsListener wrapped;

    public HierarchyBoundsListenerWrapper(@Nullable HierarchyBoundsListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void setWrapped(@Nullable HierarchyBoundsListener wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void ancestorMoved(HierarchyEvent e) {
        if(wrapped != null)
            wrapped.ancestorMoved(e);
    }

    @Override
    public void ancestorResized(HierarchyEvent e) {
        if(wrapped != null)
            wrapped.ancestorResized(e);
    }
}
