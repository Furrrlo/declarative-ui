package io.github.furrrlo.dui;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

class UpdateScheduler {

    private static final boolean TRACE_UPDATE_SCHEDULES = true;

    private final PriorityBlockingQueue<Update> updatesQueue = new PriorityBlockingQueue<>();
    private final AtomicBoolean isScheduled = new AtomicBoolean();
    private final Consumer<Runnable> frameworkScheduler;

    public UpdateScheduler(Consumer<Runnable> frameworkScheduler) {
        this.frameworkScheduler = traceUpdateSchedules(frameworkScheduler);
    }

    private static Consumer<Runnable> traceUpdateSchedules(Consumer<Runnable> updateScheduler) {
        return !TRACE_UPDATE_SCHEDULES ? updateScheduler : (update) -> {
            final Throwable trace = new Exception("Called from here");
            updateScheduler.accept(() -> {
                try {
                    update.run();
                } catch (Throwable ex) {
                    ex.addSuppressed(trace);
                    throw ex;
                }
            });
        };
    }

    public void schedule(int priority, Runnable update) {
        updatesQueue.add(new Update(priority, update));

        if(!isScheduled.getAndSet(true))
            frameworkScheduler.accept(this::onFrameworkThreadTick);
    }

    private void onFrameworkThreadTick() {
        // We set ourselves as not scheduled as soon as we get here.
        // This way we may get scheduled even if the queue is empty,
        // but we will never be not scheduled when the queue is not empty
        this.isScheduled.set(false);

        Update u;
        while((u = updatesQueue.poll()) != null)
            u.run();
    }

    static class Update implements Comparable<Update>, Runnable {
        private static final AtomicLong SEQ = new AtomicLong();

        private final int priority;
        private final long seqNum;
        private final Runnable runnable;

        public Update(int priority, Runnable runnable) {
            this.priority = priority;
            this.seqNum = SEQ.getAndIncrement();
            this.runnable = runnable;
        }

        @Override
        public int compareTo(Update other) {
            int res = Integer.compare(priority, other.priority);
            return res != 0 ? res : Long.compare(seqNum, other.seqNum);
        }

        @Override
        public void run() {
            runnable.run();
        }
    }
}
