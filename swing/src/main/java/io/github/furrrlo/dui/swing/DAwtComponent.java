package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.IdentityFreeConsumer;
import io.github.furrrlo.dui.IdentityFreeSupplier;
import io.github.furrrlo.dui.swing.accessibility.DAccessibleContext;
import io.leangen.geantyref.TypeToken;

import javax.accessibility.AccessibleContext;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.EventListener;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class DAwtComponent {
    public static class Decorator<T extends Component> extends SwingDecorator<T> {

        private static final String PREFIX = "__DAwtComponent__";

        protected Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);
        }

        protected Decorator(TypeToken<T> type, Supplier<T> factory) {
            super(type, factory);
        }

        public void maximumSize(Supplier<Dimension> dimension) {
            maximumWidth(() -> (int) dimension.get().getWidth());
            maximumHeight(() -> (int) dimension.get().getHeight());
        }

        public void name(IdentityFreeSupplier<String> name) {
            attribute(PREFIX + "name", Component::getName, Component::setName, name);
        }

        public void visible(IdentityFreeSupplier<Boolean> visible) {
            attribute(PREFIX + "visible", Component::isVisible, Component::setVisible, visible);
        }

        public void background(IdentityFreeSupplier<? extends Color> color) {
            attribute(PREFIX + "background", Component::getBackground, Component::setBackground, color);
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

        public void componentListener(ComponentListener componentListener) {
            eventListener(
                    PREFIX + "componentListener",
                    ComponentListener.class,
                    ComponentListenerWrapper::new,
                    Component::addComponentListener,
                    componentListener);
        }

        public void focusListener(FocusListener focusListener) {
            eventListener(
                    PREFIX + "focusListener",
                    FocusListener.class,
                    FocusListenerWrapper::new,
                    Component::addFocusListener,
                    focusListener);
        }

        public void hierarchyListener(HierarchyListener hierarchyListener) {
            eventListener(
                    PREFIX + "hierarchyListener",
                    HierarchyListener.class,
                    HierarchyListenerWrapper::new,
                    Component::addHierarchyListener,
                    hierarchyListener);
        }

        public void hierarchyBoundsListener(HierarchyBoundsListener hierarchyBoundsListener) {
            eventListener(
                    PREFIX + "hierarchyBoundsListener",
                    HierarchyBoundsListener.class,
                    HierarchyBoundsListenerWrapper::new,
                    Component::addHierarchyBoundsListener,
                    hierarchyBoundsListener);
        }

        public void keyListener(KeyListener keyListener) {
            eventListener(
                    PREFIX + "keyListener",
                    KeyListener.class,
                    KeyListenerWrapper::new,
                    Component::addKeyListener,
                    keyListener);
        }

        public void mouseListener(MouseListener mouseListener) {
            eventListener(
                    PREFIX + "mouseListener",
                    MouseListener.class,
                    MouseListenerWrapper::new,
                    Component::addMouseListener,
                    mouseListener);
        }

        public void mouseMotionListener(MouseMotionListener mouseMotionListener) {
            eventListener(
                    PREFIX + "mouseMotionListener",
                    MouseMotionListener.class,
                    MouseMotionListenerWrapper::new,
                    Component::addMouseMotionListener,
                    mouseMotionListener);
        }

        public void mouseWheelListener(MouseWheelListener mouseWheelListener) {
            eventListener(
                    PREFIX + "mouseWheelListener",
                    MouseWheelListener.class,
                    MouseWheelListenerWrapper::new,
                    Component::addMouseWheelListener,
                    mouseWheelListener);
        }

        public void inputMethodListener(InputMethodListener inputMethodListener) {
            eventListener(
                    PREFIX + "inputMethodListener",
                    InputMethodListener.class,
                    InputMethodListenerWrapper::new,
                    Component::addInputMethodListener,
                    inputMethodListener);
        }

        public void propertyChangeListener(PropertyChangeListener propertyChangeListener) {
            eventListener(
                    PREFIX + "propertyChangeListener",
                    PropertyChangeListener.class,
                    PropertyChangeListenerWrapper::new,
                    Component::addPropertyChangeListener,
                    propertyChangeListener);
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

        public void enabled(IdentityFreeSupplier<Boolean> enabled) {
            attribute(PREFIX + "enabled", Component::isEnabled, Component::setEnabled, enabled);
        }

        public void font(IdentityFreeSupplier<? extends Font> font) {
            attribute(PREFIX + "font", Component::getFont, Component::setFont, font);
        }

        public void foreground(IdentityFreeSupplier<? extends Color> foreground) {
            attribute(PREFIX + "foreground", Component::getForeground, Component::setForeground, foreground);
        }

        public void minimumSize(IdentityFreeSupplier<? extends Dimension> minimumSize) {
            attribute(PREFIX + "minimumSize", Component::getMinimumSize, Component::setMinimumSize, minimumSize);
        }

        public void preferredSize(IdentityFreeSupplier<? extends Dimension> preferredSize) {
            attribute(PREFIX + "preferredSize", Component::getPreferredSize, Component::setPreferredSize, preferredSize);
        }

        public void componentOrientation(IdentityFreeSupplier<ComponentOrientation> componentOrientation) {
            attribute(PREFIX + "componentOrientation", Component::getComponentOrientation, Component::setComponentOrientation, componentOrientation);
        }

        public void accessibleContext(IdentityFreeConsumer<DAccessibleContext.Decorator<AccessibleContext>> body) {
            inner(Component::getAccessibleContext, DAccessibleContext.forInner(body));
        }
    }
}
