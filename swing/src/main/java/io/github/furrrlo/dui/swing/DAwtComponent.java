package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.IdentityFreeSupplier;
import io.leangen.geantyref.TypeToken;

import java.awt.*;
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
    }
}
