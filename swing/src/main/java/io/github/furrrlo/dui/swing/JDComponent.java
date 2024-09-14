package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.*;
import io.leangen.geantyref.TypeToken;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class JDComponent {

    public static class Decorator<T extends JComponent> extends SwingDecorator<T> {

        private static final String PREFIX = "__JDComponent__";

        private final ReservedMemo<List<Child<?>>> reservedChildMemo;

        protected Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);
            reservedChildMemo = reserveMemo(Collections::emptyList);
        }

        protected Decorator(TypeToken<T> type, Supplier<T> factory) {
            super(type, factory);
            reservedChildMemo = reserveMemo(Collections::emptyList);
        }

        public void maximumSize(Supplier<Dimension> dimension) {
            maximumWidth(() -> (int) dimension.get().getWidth());
            maximumHeight(() -> (int) dimension.get().getHeight());
        }

        public void name(IdentityFreeSupplier<String> name) {
            attribute(PREFIX + "name", Component::getName, Component::setName, name);
        }

        public void visible(IdentityFreeSupplier<Boolean> visible) {
            attribute(PREFIX + "visible", JComponent::isVisible, JComponent::setVisible, visible);
        }

        public void background(IdentityFreeSupplier<? extends Color> color) {
            attribute(PREFIX + "background", JComponent::getBackground, JComponent::setBackground, color);
        }

        public void maximumWidth(IdentityFreeSupplier<Integer> width) {
            attribute(PREFIX + "maximumHeight",
                    c -> c.getMaximumSize().width,
                    (c, w) -> c.setMaximumSize(new Dimension(w, (int) c.getMaximumSize().getHeight())),
                    width);
        }

        public void maximumHeight(IdentityFreeSupplier<Integer> height) {
            attribute(PREFIX + "maximumHeight",
                    c -> c.getMaximumSize().height,
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
                        if (maybeWrapper.isPresent())
                            maybeWrapper.get().setWrapped(v);
                        else
                            adder.accept(component, (L) factory.apply(v));
                    },
                    () -> l);
        }

        public void layout(IdentityFreeSupplier<? extends LayoutManager> layoutManager) {
            attribute(PREFIX + "layout", Container::getLayout, Container::setLayout, layoutManager);
        }

        public void children(IdentityFreeConsumer<ChildCollector> childCollector) {
            final Memo<List<Child<?>>> children = reservedChildMemo.apply(IdentityFreeSupplier.explicit(() -> {
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

        public void actionMap(IdentityFreeSupplier<? extends ActionMap> actionMap) {
            attribute(PREFIX + "actionMap", JComponent::getActionMap, JComponent::setActionMap, actionMap);
        }

        public void alignmentX(IdentityFreeSupplier<Float> alignmentX) {
            attribute(PREFIX + "alignmentX", JComponent::getAlignmentX, JComponent::setAlignmentX, alignmentX);
        }

        public void alignmentY(IdentityFreeSupplier<Float> alignmentY) {
            attribute(PREFIX + "alignmentY", JComponent::getAlignmentY, JComponent::setAlignmentY, alignmentY);
        }

        public void autoscrolls(IdentityFreeSupplier<Boolean> autoscrolls) {
            attribute(PREFIX + "autoscrolls", JComponent::getAutoscrolls, JComponent::setAutoscrolls, autoscrolls);
        }

        public void border(IdentityFreeSupplier<? extends Border> border) {
            attribute(PREFIX + "border", JComponent::getBorder, JComponent::setBorder, border);
        }

        public void componentPopupMenu(
                @Nullable DeclarativeComponentSupplier<? extends JPopupMenu> componentPopupMenu) {
            fnAttribute(PREFIX + "componentPopupMenu", JComponent::getComponentPopupMenu, JComponent::setComponentPopupMenu, componentPopupMenu);
        }

        public void debugGraphicsOptions(IdentityFreeSupplier<Integer> debugGraphicsOptions) {
            attribute(PREFIX + "debugGraphicsOptions", JComponent::getDebugGraphicsOptions, JComponent::setDebugGraphicsOptions, debugGraphicsOptions);
        }

        public void doubleBuffered(IdentityFreeSupplier<Boolean> doubleBuffered) {
            attribute(PREFIX + "doubleBuffered", JComponent::isDoubleBuffered, JComponent::setDoubleBuffered, doubleBuffered);
        }

        public void enabled(IdentityFreeSupplier<Boolean> enabled) {
            attribute(PREFIX + "enabled", JComponent::isEnabled, JComponent::setEnabled, enabled);
        }

        public void font(IdentityFreeSupplier<? extends Font> font) {
            attribute(PREFIX + "font", JComponent::getFont, JComponent::setFont, font);
        }

        public void foreground(IdentityFreeSupplier<? extends Color> foreground) {
            attribute(PREFIX + "foreground", JComponent::getForeground, JComponent::setForeground, foreground);
        }

        public void inheritsPopupMenu(IdentityFreeSupplier<Boolean> inheritsPopupMenu) {
            attribute(PREFIX + "inheritsPopupMenu", JComponent::getInheritsPopupMenu, JComponent::setInheritsPopupMenu, inheritsPopupMenu);
        }

        public void inputVerifier(IdentityFreeSupplier<? extends InputVerifier> inputVerifier) {
            attribute(PREFIX + "inputVerifier", JComponent::getInputVerifier, JComponent::setInputVerifier, inputVerifier);
        }

        public void minimumSize(IdentityFreeSupplier<? extends Dimension> minimumSize) {
            attribute(PREFIX + "minimumSize", JComponent::getMinimumSize, JComponent::setMinimumSize, minimumSize);
        }

        public void opaque(IdentityFreeSupplier<Boolean> opaque) {
            attribute(PREFIX + "opaque", JComponent::isOpaque, JComponent::setOpaque, opaque);
        }

        public void preferredSize(IdentityFreeSupplier<? extends Dimension> preferredSize) {
            attribute(PREFIX + "preferredSize", JComponent::getPreferredSize, JComponent::setPreferredSize, preferredSize);
        }

        public void requestFocusEnabled(IdentityFreeSupplier<Boolean> requestFocusEnabled) {
            attribute(PREFIX + "requestFocusEnabled", JComponent::isRequestFocusEnabled, JComponent::setRequestFocusEnabled, requestFocusEnabled);
        }

        public void toolTipText(IdentityFreeSupplier<String> toolTipText) {
            attribute(PREFIX + "toolTipText", JComponent::getToolTipText, JComponent::setToolTipText, toolTipText);
        }

        public void transferHandler(IdentityFreeSupplier<? extends TransferHandler> transferHandler) {
            attribute(PREFIX + "transferHandler", JComponent::getTransferHandler, JComponent::setTransferHandler, transferHandler);
        }

        public void verifyInputWhenFocusTarget(IdentityFreeSupplier<Boolean> verifyInputWhenFocusTarget) {
            attribute(PREFIX + "verifyInputWhenFocusTarget", JComponent::getVerifyInputWhenFocusTarget, JComponent::setVerifyInputWhenFocusTarget, verifyInputWhenFocusTarget);
        }

        public void componentOrientation(IdentityFreeSupplier<ComponentOrientation> componentOrientation) {
            attribute(PREFIX + "componentOrientation", JComponent::getComponentOrientation, JComponent::setComponentOrientation, componentOrientation);
        }
    }
}
