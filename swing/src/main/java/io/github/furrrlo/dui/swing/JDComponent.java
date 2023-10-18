package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.*;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class JDComponent {

    public static class Decorator<T extends JComponent> extends SwingDecorator<T> {

        private static final String PREFIX = "__JDComponent__";

        private final ReservedMemo<List<Child<?>>> reservedChildMemo;

        protected Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);
            reservedChildMemo = reserveMemo(Collections::emptyList);
        }

        public void maximumSize(Supplier<Dimension> dimension) {
            maximumWidth(() -> (int) dimension.get().getWidth());
            maximumHeight(() -> (int) dimension.get().getHeight());
        }

        public void name(Supplier<String> name) {
            attribute(PREFIX + "name", Component::setName, name);
        }

        public void visible(Supplier<Boolean> visible) {
            attribute(PREFIX + "visible", JComponent::setVisible, visible);
        }

        public void background(Supplier<Color> color) {
            attribute(PREFIX + "background", JComponent::setBackground, color);
        }

        public void maximumWidth(Supplier<Integer> width) {
            attribute(PREFIX + "maximumHeight",
                    (c, w) -> c.setMaximumSize(new Dimension(w, (int) c.getMaximumSize().getHeight())),
                    width);
        }

        public void maximumHeight(Supplier<Integer> height) {
            attribute(PREFIX + "maximumHeight",
                    (c, h) -> c.setMaximumSize(new Dimension((int) c.getMaximumSize().getWidth(), h)),
                    height);
        }

        @SuppressWarnings("unchecked")
        public <L extends EventListener> void eventListener(String key,
                                                            Class<L> type,
                                                            Function<L, EventListenerWrapper<L>> factory,
                                                            BiConsumer<T, L> adder,
                                                            L l) {
            attribute(
                    key,
                    (component, v) -> {
                        Optional<EventListenerWrapper<L>> maybeWrapper = Arrays.stream(component.getListeners(type))
                                .filter(EventListenerWrapper.class::isInstance)
                                .map(w -> (EventListenerWrapper<L>) w)
                                .findFirst();
                        if(maybeWrapper.isPresent())
                            maybeWrapper.get().setWrapped(v);
                        else
                            adder.accept(component, (L) factory.apply(v));
                    },
                    () -> l);
        }

        public void layout(Supplier<LayoutManager> layoutManager) {
            attribute(PREFIX + "layout", Container::setLayout, layoutManager);
        }

        public void children(IdentifiableConsumer<ChildCollector> childCollector) {
            final Memo<List<Child<?>>> children = reservedChildMemo.apply(IdentifiableSupplier.explicit(() -> {
                final List<Child<?>> children0 = new ArrayList<>();
                childCollector.accept((key, comp, constraints) -> children0.add(new Child<>(key, comp, constraints)));
                return children0;
            }, childCollector.deps()));

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
    }
}
