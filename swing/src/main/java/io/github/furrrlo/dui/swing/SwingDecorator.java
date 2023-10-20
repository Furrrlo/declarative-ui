package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponentContextDecorator;
import io.github.furrrlo.dui.FrameworkScheduler;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class SwingDecorator<T> extends DeclarativeComponentContextDecorator<T> {

    private static final AtomicBoolean HAS_INSTALLED_QUEUE = new AtomicBoolean();
    private static final AtomicReference<FrameworkScheduler> SWING_UPDATE_SCHEDULER = new AtomicReference<>();

    protected SwingDecorator(Class<T> type, Supplier<T> factory) {
        super(type, factory, SwingUtilities::isEventDispatchThread, getSwingUpdateScheduler());
    }

    private static FrameworkScheduler getSwingUpdateScheduler() {
        return SWING_UPDATE_SCHEDULER.updateAndGet(prev -> prev != null ? prev : new FrameworkScheduler((update) -> {
            EventQueue eventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
            if (eventQueue instanceof PrioritizableEventQueue) {
                ((PrioritizableEventQueue) eventQueue).invokeRightAfter(update);
                return;
            }

            if(!HAS_INSTALLED_QUEUE.getAndSet(true)) {
                Toolkit.getDefaultToolkit().getSystemEventQueue().push(new PrioritizableEventQueue());
                eventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
            }

            if (eventQueue instanceof PrioritizableEventQueue) {
                ((PrioritizableEventQueue) eventQueue).invokeRightAfter(update);
                return;
            }

            SwingUtilities.invokeLater(update);
        }));
    }
}
