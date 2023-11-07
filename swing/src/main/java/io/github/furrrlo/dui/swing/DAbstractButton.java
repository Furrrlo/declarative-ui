package io.github.furrrlo.dui.swing;

import javax.swing.*;
import javax.swing.plaf.ButtonUI;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Objects;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class DAbstractButton {

    public static class Decorator<T extends AbstractButton> extends JDComponent.Decorator<T> {

        private static final String PREFIX = "__DAbstractButton__";

        protected Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);
        }

        public void icon(Supplier<? extends Icon> icon) {
            attribute(PREFIX + "icon", AbstractButton::getIcon, AbstractButton::setIcon, icon);
        }

        public void text(Supplier<String> text) {
            attribute(PREFIX + "text", AbstractButton::getText, AbstractButton::setText, text);
        }

        public void margin(Supplier<? extends Insets> m) {
            attribute(PREFIX + "margin", AbstractButton::getMargin, AbstractButton::setMargin, m);
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

        public void ui(Supplier<? extends ButtonUI> ui) {
            attribute(PREFIX + "ui", AbstractButton::getUI, AbstractButton::setUI, ui);
        }

        public void action(Supplier<? extends Action> action) {
            attribute(PREFIX + "action", AbstractButton::getAction, AbstractButton::setAction, action);
        }

        public void actionCommand(Supplier<String> actionCommand) {
            attribute(PREFIX + "actionCommand", AbstractButton::getActionCommand, AbstractButton::setActionCommand, actionCommand);
        }

        public void borderPainted(Supplier<Boolean> borderPainted) {
            attribute(PREFIX + "borderPainted", AbstractButton::isBorderPainted, AbstractButton::setBorderPainted, borderPainted);
        }

        public void contentAreaFilled(Supplier<Boolean> contentAreaFilled) {
            attribute(PREFIX + "contentAreaFilled", AbstractButton::isContentAreaFilled, AbstractButton::setContentAreaFilled, contentAreaFilled);
        }

        public void disabledIcon(Supplier<? extends Icon> disabledIcon) {
            attribute(PREFIX + "disabledIcon", AbstractButton::getDisabledIcon, AbstractButton::setDisabledIcon, disabledIcon);
        }

        public void disabledSelectedIcon(
                Supplier<? extends Icon> disabledSelectedIcon) {
            attribute(PREFIX + "disabledSelectedIcon", AbstractButton::getDisabledSelectedIcon, AbstractButton::setDisabledSelectedIcon, disabledSelectedIcon);
        }

        public void displayedMnemonicIndex(Supplier<Integer> displayedMnemonicIndex) {
            attribute(PREFIX + "displayedMnemonicIndex", AbstractButton::getDisplayedMnemonicIndex, AbstractButton::setDisplayedMnemonicIndex, displayedMnemonicIndex);
        }

        public void focusPainted(Supplier<Boolean> focusPainted) {
            attribute(PREFIX + "focusPainted", AbstractButton::isFocusPainted, AbstractButton::setFocusPainted, focusPainted);
        }

        public void hideActionText(Supplier<Boolean> hideActionText) {
            attribute(PREFIX + "hideActionText", AbstractButton::getHideActionText, AbstractButton::setHideActionText, hideActionText);
        }

        public void horizontalAlignment(
                Supplier<Integer> horizontalAlignment) {
            attribute(PREFIX + "horizontalAlignment", AbstractButton::getHorizontalAlignment, AbstractButton::setHorizontalAlignment, horizontalAlignment);
        }

        public void horizontalTextPosition(
                Supplier<Integer> horizontalTextPosition) {
            attribute(PREFIX + "horizontalTextPosition", AbstractButton::getHorizontalTextPosition, AbstractButton::setHorizontalTextPosition, horizontalTextPosition);
        }

        public void iconTextGap(Supplier<Integer> iconTextGap) {
            attribute(PREFIX + "iconTextGap", AbstractButton::getIconTextGap, AbstractButton::setIconTextGap, iconTextGap);
        }

        public void mnemonic(Supplier<Integer> mnemonic) {
            attribute(PREFIX + "mnemonic", AbstractButton::getMnemonic, AbstractButton::setMnemonic, mnemonic);
        }

        public void model(Supplier<? extends ButtonModel> model) {
            attribute(PREFIX + "model", AbstractButton::getModel, AbstractButton::setModel, model);
        }

        public void multiClickThreshhold(Supplier<Long> multiClickThreshhold) {
            attribute(PREFIX + "multiClickThreshhold", AbstractButton::getMultiClickThreshhold, AbstractButton::setMultiClickThreshhold, multiClickThreshhold);
        }

        public void pressedIcon(Supplier<? extends Icon> pressedIcon) {
            attribute(PREFIX + "pressedIcon", AbstractButton::getPressedIcon, AbstractButton::setPressedIcon, pressedIcon);
        }

        public void rolloverEnabled(Supplier<Boolean> rolloverEnabled) {
            attribute(PREFIX + "rolloverEnabled", AbstractButton::isRolloverEnabled, AbstractButton::setRolloverEnabled, rolloverEnabled);
        }

        public void rolloverIcon(Supplier<? extends Icon> rolloverIcon) {
            attribute(PREFIX + "rolloverIcon", AbstractButton::getRolloverIcon, AbstractButton::setRolloverIcon, rolloverIcon);
        }

        public void rolloverSelectedIcon(
                Supplier<? extends Icon> rolloverSelectedIcon) {
            attribute(PREFIX + "rolloverSelectedIcon", AbstractButton::getRolloverSelectedIcon, AbstractButton::setRolloverSelectedIcon, rolloverSelectedIcon);
        }

        public void selectedIcon(Supplier<? extends Icon> selectedIcon) {
            attribute(PREFIX + "selectedIcon", AbstractButton::getSelectedIcon, AbstractButton::setSelectedIcon, selectedIcon);
        }

        public void verticalAlignment(Supplier<Integer> verticalAlignment) {
            attribute(PREFIX + "verticalAlignment", AbstractButton::getVerticalAlignment, AbstractButton::setVerticalAlignment, verticalAlignment);
        }

        public void verticalTextPosition(
                Supplier<Integer> verticalTextPosition) {
            attribute(PREFIX + "verticalTextPosition", AbstractButton::getVerticalTextPosition, AbstractButton::setVerticalTextPosition, verticalTextPosition);
        }
    }
}
