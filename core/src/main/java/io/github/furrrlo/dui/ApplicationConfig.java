package io.github.furrrlo.dui;

import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
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

    private final Collection<MethodHandles.Lookup> lookups;
    private final ExecutorService launchedEffectsExecutor;

    private ApplicationConfig(Builder builder) {
        this.launchedEffectsExecutor = builder.launchedEffectsExecutor;
        this.lookups = Collections.unmodifiableSet(new LinkedHashSet<>(builder.lookups));
    }

    public static Builder builder() {
        return new ApplicationConfig.Builder();
    }

    public ExecutorService launchedEffectsExecutor() {
        return launchedEffectsExecutor;
    }

    public Collection<MethodHandles.Lookup> lookups() {
        return lookups;
    }

    public ApplicationConfig.Builder asBuilder() {
        return new Builder()
                .withLaunchedEffectsExecutor(launchedEffectsExecutor)
                .grantAccess(lookups);
    }

    public static class Builder {

        private ExecutorService launchedEffectsExecutor = DEFAULT_EXECUTOR_SERVICE;
        private Collection<MethodHandles.Lookup> lookups = new LinkedHashSet<>();

        private Builder() {
        }

        public Builder withLaunchedEffectsExecutor(ExecutorService launchedEffectsExecutor) {
            this.launchedEffectsExecutor = launchedEffectsExecutor;
            return this;
        }

        public Builder grantAccess(MethodHandles.Lookup lookup) {
            this.lookups.add(lookup);
            return this;
        }

        public Builder grantAccess(Collection<MethodHandles.Lookup> lookups) {
            this.lookups.addAll(lookups);
            return this;
        }

        public ApplicationConfig build() {
            return new ApplicationConfig(this);
        }
    }
}
