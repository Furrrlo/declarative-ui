package io.github.furrrlo.dui;

import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public interface Application {

    static void create(Consumer<Context> app) {
        final StatefulDeclarativeComponent<?, BaseApplication, ?, ?> ctx =
                DeclarativeComponentFactory.INSTANCE.of(Context::new, app::accept).doApplyInternal();
        ctx.triggerStateUpdate();
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

        public void roots(IdentifiableConsumer<RootCollector> rootCollector) {
            final Memo<List<Root<?>>> roots = reservedRootsMemo.apply(IdentifiableSupplier.explicit(() -> {
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

            default void add(DeclarativeComponentSupplier<? extends Component> comp) {
                add(null, comp);
            }

            void add(@Nullable String key, DeclarativeComponentSupplier<? extends Component> comp);
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
