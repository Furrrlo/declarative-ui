package io.github.furrrlo.dui;

import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public interface Application {

    static void create(Consumer<Context> app) {
        create(ApplicationConfig.builder().build(), app);
    }

    static void create(ApplicationConfig appConfig, Consumer<Context> app) {
        final StatefulDeclarativeComponent<BaseApplication, ?, ?> ctx = DeclarativeComponentFactory.INSTANCE
                .ofApplication(appConfig, Context::new, app::accept)
                .doApplyInternal();
        ctx.runOrScheduleOnFrameworkThread(() -> ctx.updateOrCreateComponent(appConfig, appConfig.lookups()));
    }

    class BaseApplication {

        private final List<Object> roots = new ArrayList<>();

        public List<Object> getRoots() {
            return roots;
        }
    }

    class Context extends NoFrameworkDecorator<BaseApplication> {

        private static final String PREFIX = "__Application.Context__";

        private final ReservedMemo<List<Root<?>>> reservedRootsMemo;

        protected Context() {
            super(BaseApplication.class, BaseApplication::new);
            reservedRootsMemo = reserveMemo(Collections::emptyList);
        }

        public void roots(IdentityFreeConsumer<RootCollector> rootCollector) {
            final Memo<List<Root<?>>> roots = reservedRootsMemo.apply(IdentityFreeSupplier.explicit(() -> {
                final List<Root<?>> roots0 = new ArrayList<>();
                rootCollector.accept((key, comp) -> roots0.add(new Root<>(key, comp)));
                return roots0;
            }, rootCollector));

            listFnAttribute(
                    PREFIX + "roots",
                    (app, idx, s, v) -> {
                        if(idx >= app.getRoots().size())
                            app.getRoots().add(v);
                        else
                            app.getRoots().add(idx, v);
                    },
                    (app, idx) -> app.getRoots().remove(idx),
                    roots);
        }

        public interface RootCollector {

            default void add(DeclarativeComponentSupplier<?> comp) {
                add(null, comp);
            }

            void add(@Nullable String key, DeclarativeComponentSupplier<?> comp);
        }

        private static class Root<T> implements DeclarativeComponentWithIdSupplier<T> {

            private final @Nullable String id;
            private final DeclarativeComponentSupplier<T> component;

            public Root(@Nullable String id, DeclarativeComponentSupplier<T> component) {
                this.id = id;
                this.component = component;
            }

            @Override
            public DeclarativeComponent<T> apply(DeclarativeComponentFactory factory) {
                return component.apply(factory);
            }

            @Override
            public @Nullable String getId() {
                return id;
            }

            @Override
            public String toString() {
                return "Root{" +
                        "id='" + id + '\'' +
                        ", component=" + component +
                        '}';
            }
        }
    }
}
