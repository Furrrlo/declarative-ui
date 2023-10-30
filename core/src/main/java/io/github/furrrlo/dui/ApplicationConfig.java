package io.github.furrrlo.dui;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

public class ApplicationConfig {

    private static final ExecutorService DEFAULT_EXECUTOR_SERVICE;
    static {
        DEFAULT_EXECUTOR_SERVICE = ForkJoinPool.commonPool();
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
