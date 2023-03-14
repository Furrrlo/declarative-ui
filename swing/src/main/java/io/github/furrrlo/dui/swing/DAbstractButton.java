package io.github.furrrlo.dui.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.function.Supplier;

public class DAbstractButton {

    public static class Decorator <T extends AbstractButton> extends JDComponent.Decorator<T> {

        private static final String PREFIX = "__DAbstractButton__";

        protected Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);
        }

        public void icon(Icon icon) {
            attribute(PREFIX + "icon", AbstractButton::setIcon, icon);
        }

        public void text(String text) {
            attribute(PREFIX + "text", AbstractButton::setText, text);
        }

        public void margin(Insets m) {
            attribute(PREFIX + "margin", AbstractButton::setMargin, m);
        }

        public void toolTipText(String tooltip) {
            attribute(PREFIX + "toolTipText", AbstractButton::setToolTipText, tooltip);
        }

        public void enabled(boolean enabled) {
            attribute(PREFIX + "enabled", AbstractButton::setEnabled, enabled);
        }

        public void selected(boolean selected) {
            attribute(PREFIX + "selected", AbstractButton::setSelected, selected);
        }

        public void buttonGroup(ButtonGroup buttonGroup) {
            attribute(PREFIX + "buttonGroup",
                    (btn, group) -> group.add(btn),
                    buttonGroup);
        }

        public void actionListener(ActionListener l) {
            eventListener(PREFIX + "actionListener",
                    ActionListener.class,
                    ActionListenerWrapper::new,
                    AbstractButton::addActionListener,
                    l);
        }
    }
}
