package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.IdentityFreeSupplier;

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

        public void icon(IdentityFreeSupplier<? extends Icon> icon) {
            attribute(PREFIX + "icon", AbstractButton::getIcon, AbstractButton::setIcon, icon);
        }

        public void text(IdentityFreeSupplier<String> text) {
            attribute(PREFIX + "text", AbstractButton::getText, AbstractButton::setText, text);
        }

        public void margin(IdentityFreeSupplier<? extends Insets> m) {
            attribute(PREFIX + "margin", AbstractButton::getMargin, AbstractButton::setMargin, m);
        }

        public void selected(IdentityFreeSupplier<Boolean> selected) {
            attribute(PREFIX + "selected", AbstractButton::setSelected, selected,
                    // For stuff that toggles, to avoid having to cancel events
                    (c, oldV, newV) -> Objects.equals(c.isSelected(), newV));
        }

        public void buttonGroup(IdentityFreeSupplier<? extends ButtonGroup> buttonGroup) {
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

        public void ui(IdentityFreeSupplier<? extends ButtonUI> ui) {
            attribute(PREFIX + "ui", AbstractButton::getUI, AbstractButton::setUI, ui);
        }

        public void action(IdentityFreeSupplier<? extends Action> action) {
            attribute(PREFIX + "action", AbstractButton::getAction, AbstractButton::setAction, action);
        }

        public void actionCommand(IdentityFreeSupplier<String> actionCommand) {
            attribute(PREFIX + "actionCommand", AbstractButton::getActionCommand, AbstractButton::setActionCommand, actionCommand);
        }

        public void borderPainted(IdentityFreeSupplier<Boolean> borderPainted) {
            attribute(PREFIX + "borderPainted", AbstractButton::isBorderPainted, AbstractButton::setBorderPainted, borderPainted);
        }

        public void contentAreaFilled(IdentityFreeSupplier<Boolean> contentAreaFilled) {
            attribute(PREFIX + "contentAreaFilled", AbstractButton::isContentAreaFilled, AbstractButton::setContentAreaFilled, contentAreaFilled);
        }

        public void disabledIcon(IdentityFreeSupplier<? extends Icon> disabledIcon) {
            attribute(PREFIX + "disabledIcon", AbstractButton::getDisabledIcon, AbstractButton::setDisabledIcon, disabledIcon);
        }

        public void disabledSelectedIcon(IdentityFreeSupplier<? extends Icon> disabledSelectedIcon) {
            attribute(PREFIX + "disabledSelectedIcon", AbstractButton::getDisabledSelectedIcon, AbstractButton::setDisabledSelectedIcon, disabledSelectedIcon);
        }

        public void displayedMnemonicIndex(IdentityFreeSupplier<Integer> displayedMnemonicIndex) {
            attribute(PREFIX + "displayedMnemonicIndex", AbstractButton::getDisplayedMnemonicIndex, AbstractButton::setDisplayedMnemonicIndex, displayedMnemonicIndex);
        }

        public void focusPainted(IdentityFreeSupplier<Boolean> focusPainted) {
            attribute(PREFIX + "focusPainted", AbstractButton::isFocusPainted, AbstractButton::setFocusPainted, focusPainted);
        }

        public void hideActionText(IdentityFreeSupplier<Boolean> hideActionText) {
            attribute(PREFIX + "hideActionText", AbstractButton::getHideActionText, AbstractButton::setHideActionText, hideActionText);
        }

        public void horizontalAlignment(IdentityFreeSupplier<Integer> horizontalAlignment) {
            attribute(PREFIX + "horizontalAlignment", AbstractButton::getHorizontalAlignment, AbstractButton::setHorizontalAlignment, horizontalAlignment);
        }

        public void horizontalTextPosition(IdentityFreeSupplier<Integer> horizontalTextPosition) {
            attribute(PREFIX + "horizontalTextPosition", AbstractButton::getHorizontalTextPosition, AbstractButton::setHorizontalTextPosition, horizontalTextPosition);
        }

        public void iconTextGap(IdentityFreeSupplier<Integer> iconTextGap) {
            attribute(PREFIX + "iconTextGap", AbstractButton::getIconTextGap, AbstractButton::setIconTextGap, iconTextGap);
        }

        public void mnemonic(IdentityFreeSupplier<Integer> mnemonic) {
            attribute(PREFIX + "mnemonic", AbstractButton::getMnemonic, AbstractButton::setMnemonic, mnemonic);
        }

        public void model(IdentityFreeSupplier<? extends ButtonModel> model) {
            attribute(PREFIX + "model", AbstractButton::getModel, AbstractButton::setModel, model);
        }

        public void multiClickThreshhold(IdentityFreeSupplier<Long> multiClickThreshhold) {
            attribute(PREFIX + "multiClickThreshhold", AbstractButton::getMultiClickThreshhold, AbstractButton::setMultiClickThreshhold, multiClickThreshhold);
        }

        public void pressedIcon(IdentityFreeSupplier<? extends Icon> pressedIcon) {
            attribute(PREFIX + "pressedIcon", AbstractButton::getPressedIcon, AbstractButton::setPressedIcon, pressedIcon);
        }

        public void rolloverEnabled(IdentityFreeSupplier<Boolean> rolloverEnabled) {
            attribute(PREFIX + "rolloverEnabled", AbstractButton::isRolloverEnabled, AbstractButton::setRolloverEnabled, rolloverEnabled);
        }

        public void rolloverIcon(IdentityFreeSupplier<? extends Icon> rolloverIcon) {
            attribute(PREFIX + "rolloverIcon", AbstractButton::getRolloverIcon, AbstractButton::setRolloverIcon, rolloverIcon);
        }

        public void rolloverSelectedIcon(IdentityFreeSupplier<? extends Icon> rolloverSelectedIcon) {
            attribute(PREFIX + "rolloverSelectedIcon", AbstractButton::getRolloverSelectedIcon, AbstractButton::setRolloverSelectedIcon, rolloverSelectedIcon);
        }

        public void selectedIcon(IdentityFreeSupplier<? extends Icon> selectedIcon) {
            attribute(PREFIX + "selectedIcon", AbstractButton::getSelectedIcon, AbstractButton::setSelectedIcon, selectedIcon);
        }

        public void verticalAlignment(IdentityFreeSupplier<Integer> verticalAlignment) {
            attribute(PREFIX + "verticalAlignment", AbstractButton::getVerticalAlignment, AbstractButton::setVerticalAlignment, verticalAlignment);
        }

        public void verticalTextPosition(IdentityFreeSupplier<Integer> verticalTextPosition) {
            attribute(PREFIX + "verticalTextPosition", AbstractButton::getVerticalTextPosition, AbstractButton::setVerticalTextPosition, verticalTextPosition);
        }
    }
}
