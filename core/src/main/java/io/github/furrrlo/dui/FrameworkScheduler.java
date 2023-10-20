package io.github.furrrlo.dui;

import java.util.function.Consumer;

public class FrameworkScheduler {

    final UpdateScheduler updateScheduler;

    public FrameworkScheduler(Consumer<Runnable> schedule) {
        this.updateScheduler = new UpdateScheduler(schedule);
    }
}
