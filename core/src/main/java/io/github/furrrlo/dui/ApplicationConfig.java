package io.github.furrrlo.dui;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ApplicationConfig {

    private static final ExecutorService DEFAULT_EXECUTOR_SERVICE;
    static {
        ExecutorService defaultExecutorService;
        try {
            // Try to use virtual threads if available
            Method m = Executors.class.getMethod("newVirtualThreadPerTaskExecutor");
            defaultExecutorService = (ExecutorService) m.invoke(null);
        } catch (Exception ex) {
            defaultExecutorService = Executors.newCachedThreadPool(new ThreadFactory() {
                private final AtomicInteger count = new AtomicInteger();

                @Override
                public Thread newThread(@NotNull Runnable r) {
                    Thread th = Executors.defaultThreadFactory().newThread(r);
                    th.setDaemon(true);
                    th.setName("dui-launched-effects-pool-" + count.getAndIncrement());
                    return th;
                }
            });
        }

        DEFAULT_EXECUTOR_SERVICE = defaultExecutorService;
    }

    private final ExecutorService launchedEffectsExecutor;

    private ApplicationConfig(Builder builder) {
        this.launchedEffectsExecutor = builder.launchedEffectsExecutor;
    }

    public static Builder builder() {
        return new ApplicationConfig.Builder();
    }

    public ExecutorService launchedEffectsExecutor() {
        return launchedEffectsExecutor;
    }

    public ApplicationConfig.Builder asBuilder() {
        return new Builder()
                .withLaunchedEffectsExecutor(launchedEffectsExecutor);
    }

    public static class Builder {

        private ExecutorService launchedEffectsExecutor = DEFAULT_EXECUTOR_SERVICE;

        private Builder() {
        }

        public Builder withLaunchedEffectsExecutor(ExecutorService launchedEffectsExecutor) {
            this.launchedEffectsExecutor = launchedEffectsExecutor;
            return this;
        }

        public ApplicationConfig build() {
            return new ApplicationConfig(this);
        }
    }
}
