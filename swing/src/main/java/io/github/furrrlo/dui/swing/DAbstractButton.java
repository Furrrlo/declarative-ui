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

        public void icon(Supplier<? extends Icon> icon) {
            attribute(PREFIX + "icon", AbstractButton::setIcon, icon);
        }

        public void text(Supplier<String> text) {
            attribute(PREFIX + "text", AbstractButton::setText, text);
        }

        public void margin(Supplier<? extends Insets> m) {
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

        public void buttonGroup(Supplier<? extends ButtonGroup> buttonGroup) {
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
    
public void ui(java.util.function.Supplier<? extends javax.swing.plaf.ButtonUI> ui) {
  attribute(PREFIX + "ui", javax.swing.AbstractButton::getUI, javax.swing.AbstractButton::setUI, ui);
}

public void action(java.util.function.Supplier<? extends javax.swing.Action> action) {
  attribute(PREFIX + "action", javax.swing.AbstractButton::getAction, javax.swing.AbstractButton::setAction, action);
}

public void actionCommand(java.util.function.Supplier<java.lang.String> actionCommand) {
  attribute(PREFIX + "actionCommand", javax.swing.AbstractButton::getActionCommand, javax.swing.AbstractButton::setActionCommand, actionCommand);
}

public void borderPainted(java.util.function.Supplier<java.lang.Boolean> borderPainted) {
  attribute(PREFIX + "borderPainted", javax.swing.AbstractButton::isBorderPainted, javax.swing.AbstractButton::setBorderPainted, borderPainted);
}

public void contentAreaFilled(java.util.function.Supplier<java.lang.Boolean> contentAreaFilled) {
  attribute(PREFIX + "contentAreaFilled", javax.swing.AbstractButton::isContentAreaFilled, javax.swing.AbstractButton::setContentAreaFilled, contentAreaFilled);
}

public void disabledIcon(java.util.function.Supplier<? extends javax.swing.Icon> disabledIcon) {
  attribute(PREFIX + "disabledIcon", javax.swing.AbstractButton::getDisabledIcon, javax.swing.AbstractButton::setDisabledIcon, disabledIcon);
}

public void disabledSelectedIcon(
    java.util.function.Supplier<? extends javax.swing.Icon> disabledSelectedIcon) {
  attribute(PREFIX + "disabledSelectedIcon", javax.swing.AbstractButton::getDisabledSelectedIcon, javax.swing.AbstractButton::setDisabledSelectedIcon, disabledSelectedIcon);
}

public void displayedMnemonicIndex(
    java.util.function.Supplier<java.lang.Integer> displayedMnemonicIndex) {
  attribute(PREFIX + "displayedMnemonicIndex", javax.swing.AbstractButton::getDisplayedMnemonicIndex, javax.swing.AbstractButton::setDisplayedMnemonicIndex, displayedMnemonicIndex);
}

public void focusPainted(java.util.function.Supplier<java.lang.Boolean> focusPainted) {
  attribute(PREFIX + "focusPainted", javax.swing.AbstractButton::isFocusPainted, javax.swing.AbstractButton::setFocusPainted, focusPainted);
}

public void hideActionText(java.util.function.Supplier<java.lang.Boolean> hideActionText) {
  attribute(PREFIX + "hideActionText", javax.swing.AbstractButton::getHideActionText, javax.swing.AbstractButton::setHideActionText, hideActionText);
}

public void horizontalAlignment(
    java.util.function.Supplier<java.lang.Integer> horizontalAlignment) {
  attribute(PREFIX + "horizontalAlignment", javax.swing.AbstractButton::getHorizontalAlignment, javax.swing.AbstractButton::setHorizontalAlignment, horizontalAlignment);
}

public void horizontalTextPosition(
    java.util.function.Supplier<java.lang.Integer> horizontalTextPosition) {
  attribute(PREFIX + "horizontalTextPosition", javax.swing.AbstractButton::getHorizontalTextPosition, javax.swing.AbstractButton::setHorizontalTextPosition, horizontalTextPosition);
}

public void iconTextGap(java.util.function.Supplier<java.lang.Integer> iconTextGap) {
  attribute(PREFIX + "iconTextGap", javax.swing.AbstractButton::getIconTextGap, javax.swing.AbstractButton::setIconTextGap, iconTextGap);
}

public void layout(java.util.function.Supplier<? extends java.awt.LayoutManager> layout) {
  attribute(PREFIX + "layout", javax.swing.AbstractButton::setLayout, layout);
}

public void mnemonic(java.util.function.Supplier<java.lang.Integer> mnemonic) {
  attribute(PREFIX + "mnemonic", javax.swing.AbstractButton::getMnemonic, javax.swing.AbstractButton::setMnemonic, mnemonic);
}

public void model(java.util.function.Supplier<? extends javax.swing.ButtonModel> model) {
  attribute(PREFIX + "model", javax.swing.AbstractButton::getModel, javax.swing.AbstractButton::setModel, model);
}

public void multiClickThreshhold(java.util.function.Supplier<java.lang.Long> multiClickThreshhold) {
  attribute(PREFIX + "multiClickThreshhold", javax.swing.AbstractButton::getMultiClickThreshhold, javax.swing.AbstractButton::setMultiClickThreshhold, multiClickThreshhold);
}

public void pressedIcon(java.util.function.Supplier<? extends javax.swing.Icon> pressedIcon) {
  attribute(PREFIX + "pressedIcon", javax.swing.AbstractButton::getPressedIcon, javax.swing.AbstractButton::setPressedIcon, pressedIcon);
}

public void rolloverEnabled(java.util.function.Supplier<java.lang.Boolean> rolloverEnabled) {
  attribute(PREFIX + "rolloverEnabled", javax.swing.AbstractButton::isRolloverEnabled, javax.swing.AbstractButton::setRolloverEnabled, rolloverEnabled);
}

public void rolloverIcon(java.util.function.Supplier<? extends javax.swing.Icon> rolloverIcon) {
  attribute(PREFIX + "rolloverIcon", javax.swing.AbstractButton::getRolloverIcon, javax.swing.AbstractButton::setRolloverIcon, rolloverIcon);
}

public void rolloverSelectedIcon(
    java.util.function.Supplier<? extends javax.swing.Icon> rolloverSelectedIcon) {
  attribute(PREFIX + "rolloverSelectedIcon", javax.swing.AbstractButton::getRolloverSelectedIcon, javax.swing.AbstractButton::setRolloverSelectedIcon, rolloverSelectedIcon);
}

public void selectedIcon(java.util.function.Supplier<? extends javax.swing.Icon> selectedIcon) {
  attribute(PREFIX + "selectedIcon", javax.swing.AbstractButton::getSelectedIcon, javax.swing.AbstractButton::setSelectedIcon, selectedIcon);
}

public void verticalAlignment(java.util.function.Supplier<java.lang.Integer> verticalAlignment) {
  attribute(PREFIX + "verticalAlignment", javax.swing.AbstractButton::getVerticalAlignment, javax.swing.AbstractButton::setVerticalAlignment, verticalAlignment);
}

public void verticalTextPosition(
    java.util.function.Supplier<java.lang.Integer> verticalTextPosition) {
  attribute(PREFIX + "verticalTextPosition", javax.swing.AbstractButton::getVerticalTextPosition, javax.swing.AbstractButton::setVerticalTextPosition, verticalTextPosition);
}
}
}
