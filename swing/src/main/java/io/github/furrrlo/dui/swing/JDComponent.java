package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.*;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.BiConsumer;
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

        public void background(Supplier<? extends Color> color) {
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

        public void layout(Supplier<? extends LayoutManager> layoutManager) {
            attribute(PREFIX + "layout", Container::setLayout, layoutManager);
        }

        public void children(IdentifiableConsumer<ChildCollector> childCollector) {
            final Memo<List<Child<?>>> children = reservedChildMemo.apply(IdentifiableSupplier.explicit(() -> {
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
    
public void actionMap(java.util.function.Supplier<? extends javax.swing.ActionMap> actionMap) {
  attribute(PREFIX + "actionMap", javax.swing.JComponent::getActionMap, javax.swing.JComponent::setActionMap, actionMap);
}

public void alignmentX(java.util.function.Supplier<java.lang.Float> alignmentX) {
  attribute(PREFIX + "alignmentX", javax.swing.JComponent::getAlignmentX, javax.swing.JComponent::setAlignmentX, alignmentX);
}

public void alignmentY(java.util.function.Supplier<java.lang.Float> alignmentY) {
  attribute(PREFIX + "alignmentY", javax.swing.JComponent::getAlignmentY, javax.swing.JComponent::setAlignmentY, alignmentY);
}

public void autoscrolls(java.util.function.Supplier<java.lang.Boolean> autoscrolls) {
  attribute(PREFIX + "autoscrolls", javax.swing.JComponent::getAutoscrolls, javax.swing.JComponent::setAutoscrolls, autoscrolls);
}

public void border(java.util.function.Supplier<? extends javax.swing.border.Border> border) {
  attribute(PREFIX + "border", javax.swing.JComponent::getBorder, javax.swing.JComponent::setBorder, border);
}

public void componentPopupMenu(
    io.github.furrrlo.dui. @org.jetbrains.annotations.Nullable DeclarativeComponentSupplier<? extends javax.swing.JPopupMenu> componentPopupMenu) {
  fnAttribute(PREFIX + "componentPopupMenu", javax.swing.JComponent::getComponentPopupMenu, javax.swing.JComponent::setComponentPopupMenu, componentPopupMenu);
}

public void debugGraphicsOptions(
    java.util.function.Supplier<java.lang.Integer> debugGraphicsOptions) {
  attribute(PREFIX + "debugGraphicsOptions", javax.swing.JComponent::getDebugGraphicsOptions, javax.swing.JComponent::setDebugGraphicsOptions, debugGraphicsOptions);
}

public void doubleBuffered(java.util.function.Supplier<java.lang.Boolean> doubleBuffered) {
  attribute(PREFIX + "doubleBuffered", javax.swing.JComponent::isDoubleBuffered, javax.swing.JComponent::setDoubleBuffered, doubleBuffered);
}

public void enabled(java.util.function.Supplier<java.lang.Boolean> enabled) {
  attribute(PREFIX + "enabled", javax.swing.JComponent::setEnabled, enabled);
}

public void font(java.util.function.Supplier<? extends java.awt.Font> font) {
  attribute(PREFIX + "font", javax.swing.JComponent::setFont, font);
}

public void foreground(java.util.function.Supplier<? extends java.awt.Color> foreground) {
  attribute(PREFIX + "foreground", javax.swing.JComponent::setForeground, foreground);
}

public void inheritsPopupMenu(java.util.function.Supplier<java.lang.Boolean> inheritsPopupMenu) {
  attribute(PREFIX + "inheritsPopupMenu", javax.swing.JComponent::getInheritsPopupMenu, javax.swing.JComponent::setInheritsPopupMenu, inheritsPopupMenu);
}

public void inputVerifier(
    java.util.function.Supplier<? extends javax.swing.InputVerifier> inputVerifier) {
  attribute(PREFIX + "inputVerifier", javax.swing.JComponent::getInputVerifier, javax.swing.JComponent::setInputVerifier, inputVerifier);
}

public void maximumSize(java.util.function.Supplier<? extends java.awt.Dimension> maximumSize) {
  attribute(PREFIX + "maximumSize", javax.swing.JComponent::getMaximumSize, javax.swing.JComponent::setMaximumSize, maximumSize);
}

public void minimumSize(java.util.function.Supplier<? extends java.awt.Dimension> minimumSize) {
  attribute(PREFIX + "minimumSize", javax.swing.JComponent::getMinimumSize, javax.swing.JComponent::setMinimumSize, minimumSize);
}

public void opaque(java.util.function.Supplier<java.lang.Boolean> opaque) {
  attribute(PREFIX + "opaque", javax.swing.JComponent::isOpaque, javax.swing.JComponent::setOpaque, opaque);
}

public void preferredSize(java.util.function.Supplier<? extends java.awt.Dimension> preferredSize) {
  attribute(PREFIX + "preferredSize", javax.swing.JComponent::getPreferredSize, javax.swing.JComponent::setPreferredSize, preferredSize);
}

public void requestFocusEnabled(
    java.util.function.Supplier<java.lang.Boolean> requestFocusEnabled) {
  attribute(PREFIX + "requestFocusEnabled", javax.swing.JComponent::isRequestFocusEnabled, javax.swing.JComponent::setRequestFocusEnabled, requestFocusEnabled);
}

public void toolTipText(java.util.function.Supplier<java.lang.String> toolTipText) {
  attribute(PREFIX + "toolTipText", javax.swing.JComponent::getToolTipText, javax.swing.JComponent::setToolTipText, toolTipText);
}

public void transferHandler(
    java.util.function.Supplier<? extends javax.swing.TransferHandler> transferHandler) {
  attribute(PREFIX + "transferHandler", javax.swing.JComponent::getTransferHandler, javax.swing.JComponent::setTransferHandler, transferHandler);
}

public void verifyInputWhenFocusTarget(
    java.util.function.Supplier<java.lang.Boolean> verifyInputWhenFocusTarget) {
  attribute(PREFIX + "verifyInputWhenFocusTarget", javax.swing.JComponent::getVerifyInputWhenFocusTarget, javax.swing.JComponent::setVerifyInputWhenFocusTarget, verifyInputWhenFocusTarget);
}
}
}
