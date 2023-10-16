package io.github.furrrlo.dui.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Objects;
import java.util.function.Supplier;

public class DAbstractButton {

    public static class Decorator <T extends AbstractButton> extends JDComponent.Decorator<T> {

        private static final String PREFIX = "__DAbstractButton__";

        protected Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);
        }

        public void icon(Supplier<Icon> icon) {
            attribute(PREFIX + "icon", AbstractButton::setIcon, icon);
        }

        public void text(Supplier<String> text) {
            attribute(PREFIX + "text", AbstractButton::setText, text);
        }

        public void margin(Supplier<Insets> m) {
            attribute(PREFIX + "margin", AbstractButton::setMargin, m);
        }

        public void toolTipText(Supplier<String> tooltip) {
            attribute(PREFIX + "toolTipText", AbstractButton::setToolTipText, tooltip);
        }

        public void enabled(Supplier<Boolean> enabled) {
            attribute(PREFIX + "enabled", AbstractButton::setEnabled, enabled);
        }

        public void selected(Supplier<Boolean> selected) {
            attribute(PREFIX + "selected", AbstractButton::setSelected, selected,
                    // For stuff that toggles, to avoid having to cancel events
                    (c, oldV, newV) -> Objects.equals(c.isSelected(), newV));
        }

        public void buttonGroup(Supplier<ButtonGroup> buttonGroup) {
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
