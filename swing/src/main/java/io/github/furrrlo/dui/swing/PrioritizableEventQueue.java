package io.github.furrrlo.dui.swing;

import java.awt.*;
import java.awt.event.InvocationEvent;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

class PrioritizableEventQueue extends EventQueue {

    private final Queue<AWTEvent> highestPriorityQueue = new ConcurrentLinkedQueue<>();

    public void invokeRightAfter(Runnable runnable) {
        highestPriorityQueue.add(new InvocationEvent(Toolkit.getDefaultToolkit(), runnable));
    }

    @Override
    public AWTEvent getNextEvent() throws InterruptedException {
        final AWTEvent evt;
        if((evt = highestPriorityQueue.poll()) != null)
            return evt;

        return super.getNextEvent();
    }
}
