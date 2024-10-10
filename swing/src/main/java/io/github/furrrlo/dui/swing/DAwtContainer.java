package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.*;
import io.leangen.geantyref.TypeToken;
import org.jspecify.annotations.Nullable;

import java.awt.*;
import java.awt.event.ContainerListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DAwtContainer {
    public static class Decorator<T extends Container> extends DAwtComponent.Decorator<T> {

        private static final String PREFIX = "__DAwtContainer__";

        private final ReservedMemo<List<Child<?>>> reservedChildMemo;
        protected Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);
            reservedChildMemo = reserveMemo(Collections::emptyList);
        }

        protected Decorator(TypeToken<T> type, Supplier<T> factory) {
            super(type, factory);
            reservedChildMemo = reserveMemo(Collections::emptyList);
        }

        public void layout(IdentityFreeSupplier<? extends LayoutManager> layoutManager) {
            attribute(PREFIX + "layout", Container::getLayout, Container::setLayout, layoutManager);
        }

        public void children(IdentityFreeConsumer<ChildCollector> childCollector) {
            final Memo<java.util.List<Child<?>>> children = reservedChildMemo.apply(IdentityFreeSupplier.explicit(() -> {
                final List<Child<?>> children0 = new ArrayList<>();
                childCollector.accept((key, comp, constraints) -> children0.add(new Child<>(key, comp, constraints)));
                return children0;
            }, childCollector));

            listFnAttribute(
                    PREFIX + "children",
                    (T p, int idx, Child<?> s, Component v) -> {
                        if (idx >= p.getComponentCount())
                            p.add(v, s.constraints);
                        else
                            p.add(v, s.constraints, idx);
                    },
                    Container::remove,
                    children);
            listAttribute(
                    PREFIX + "constraints",
                    Object.class,
                    (p, idx, s, constraints) -> {
                        final Component c = p.getComponent(idx);
                        if (p.getLayout() instanceof GridBagLayout &&
                                constraints instanceof GridBagConstraints) {
                            ((GridBagLayout) p.getLayout()).setConstraints(c, (GridBagConstraints) constraints);
                            return;
                        }

                        p.remove(idx);
                        p.add(c, constraints, idx);
                    },
                    () -> children.get().stream().map(Child::constraints).collect(Collectors.toList()));
        }

        @SuppressWarnings("overloads") // It's appropriate
        public interface ChildCollector {

            default void add(DeclarativeComponentSupplier<? extends Component> comp) {
                add(null, comp, null);
            }

            default void add(String key, DeclarativeComponentSupplier<? extends Component> comp) {
                add(key, comp, null);
            }

            default void add(DeclarativeComponentSupplier<? extends Component> comp, @Nullable Object constraints) {
                add(null, comp, constraints);
            }

            void add(@Nullable String key, DeclarativeComponentSupplier<? extends Component> comp, @Nullable Object constraints);

            default void add(IdentityFreeConsumer<ChildProps> propsFn) {
                ChildProps props = new ChildProps(propsFn);
                add(props.key, props.comp, props.constraints);
            }
        }

        public static class ChildProps {
            public DeclarativeComponentSupplier<? extends Component> comp;
            public @Nullable String key;
            public @Nullable Object constraints;

            @SuppressWarnings({"DataFlowIssue", "SelfAssignment"}) // Done on purpose
            private ChildProps(IdentityFreeConsumer<ChildProps> propsFn) {
                propsFn.accept(this);
                comp = Objects.requireNonNull(comp, "Missing child component");
            }
        }

        private static class Child<T extends Component> implements DeclarativeComponentWithIdSupplier<T> {

            private final @Nullable String id;
            private final DeclarativeComponentSupplier<T> component;
            private final @Nullable Object constraints;

            public Child(@Nullable String id, DeclarativeComponentSupplier<T> component, @Nullable Object constraints) {
                this.id = id;
                this.component = component;
                this.constraints = constraints;
            }

            public @Nullable Object constraints() {
                return constraints;
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
                return "Child{" +
                        "id='" + id + '\'' +
                        ", component=" + component +
                        ", constraints=" + constraints +
                        '}';
            }
        }

        public void containerListener(ContainerListener containerListener) {
            eventListener(
                    PREFIX + "containerListener",
                    ContainerListener.class,
                    ContainerListenerWrapper::new,
                    Container::addContainerListener,
                    containerListener);
        }
    }
}
