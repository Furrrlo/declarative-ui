package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.IdentifiableSupplier;

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

        public void icon(IdentifiableSupplier<? extends Icon> icon) {
            attribute(PREFIX + "icon", AbstractButton::getIcon, AbstractButton::setIcon, icon);
        }

        public void text(IdentifiableSupplier<String> text) {
            attribute(PREFIX + "text", AbstractButton::getText, AbstractButton::setText, text);
        }

        public void margin(IdentifiableSupplier<? extends Insets> m) {
            attribute(PREFIX + "margin", AbstractButton::getMargin, AbstractButton::setMargin, m);
        }

        public void selected(IdentifiableSupplier<Boolean> selected) {
            attribute(PREFIX + "selected", AbstractButton::setSelected, selected,
                    // For stuff that toggles, to avoid having to cancel events
                    (c, oldV, newV) -> Objects.equals(c.isSelected(), newV));
        }

        public void buttonGroup(IdentifiableSupplier<? extends ButtonGroup> buttonGroup) {
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

        public void ui(IdentifiableSupplier<? extends ButtonUI> ui) {
            attribute(PREFIX + "ui", AbstractButton::getUI, AbstractButton::setUI, ui);
        }

        public void action(IdentifiableSupplier<? extends Action> action) {
            attribute(PREFIX + "action", AbstractButton::getAction, AbstractButton::setAction, action);
        }

        public void actionCommand(IdentifiableSupplier<String> actionCommand) {
            attribute(PREFIX + "actionCommand", AbstractButton::getActionCommand, AbstractButton::setActionCommand, actionCommand);
        }

        public void borderPainted(IdentifiableSupplier<Boolean> borderPainted) {
            attribute(PREFIX + "borderPainted", AbstractButton::isBorderPainted, AbstractButton::setBorderPainted, borderPainted);
        }

        public void contentAreaFilled(IdentifiableSupplier<Boolean> contentAreaFilled) {
            attribute(PREFIX + "contentAreaFilled", AbstractButton::isContentAreaFilled, AbstractButton::setContentAreaFilled, contentAreaFilled);
        }

        public void disabledIcon(IdentifiableSupplier<? extends Icon> disabledIcon) {
            attribute(PREFIX + "disabledIcon", AbstractButton::getDisabledIcon, AbstractButton::setDisabledIcon, disabledIcon);
        }

        public void disabledSelectedIcon(IdentifiableSupplier<? extends Icon> disabledSelectedIcon) {
            attribute(PREFIX + "disabledSelectedIcon", AbstractButton::getDisabledSelectedIcon, AbstractButton::setDisabledSelectedIcon, disabledSelectedIcon);
        }

        public void displayedMnemonicIndex(IdentifiableSupplier<Integer> displayedMnemonicIndex) {
            attribute(PREFIX + "displayedMnemonicIndex", AbstractButton::getDisplayedMnemonicIndex, AbstractButton::setDisplayedMnemonicIndex, displayedMnemonicIndex);
        }

        public void focusPainted(IdentifiableSupplier<Boolean> focusPainted) {
            attribute(PREFIX + "focusPainted", AbstractButton::isFocusPainted, AbstractButton::setFocusPainted, focusPainted);
        }

        public void hideActionText(IdentifiableSupplier<Boolean> hideActionText) {
            attribute(PREFIX + "hideActionText", AbstractButton::getHideActionText, AbstractButton::setHideActionText, hideActionText);
        }

        public void horizontalAlignment(IdentifiableSupplier<Integer> horizontalAlignment) {
            attribute(PREFIX + "horizontalAlignment", AbstractButton::getHorizontalAlignment, AbstractButton::setHorizontalAlignment, horizontalAlignment);
        }

        public void horizontalTextPosition(IdentifiableSupplier<Integer> horizontalTextPosition) {
            attribute(PREFIX + "horizontalTextPosition", AbstractButton::getHorizontalTextPosition, AbstractButton::setHorizontalTextPosition, horizontalTextPosition);
        }

        public void iconTextGap(IdentifiableSupplier<Integer> iconTextGap) {
            attribute(PREFIX + "iconTextGap", AbstractButton::getIconTextGap, AbstractButton::setIconTextGap, iconTextGap);
        }

        public void mnemonic(IdentifiableSupplier<Integer> mnemonic) {
            attribute(PREFIX + "mnemonic", AbstractButton::getMnemonic, AbstractButton::setMnemonic, mnemonic);
        }

        public void model(IdentifiableSupplier<? extends ButtonModel> model) {
            attribute(PREFIX + "model", AbstractButton::getModel, AbstractButton::setModel, model);
        }

        public void multiClickThreshhold(IdentifiableSupplier<Long> multiClickThreshhold) {
            attribute(PREFIX + "multiClickThreshhold", AbstractButton::getMultiClickThreshhold, AbstractButton::setMultiClickThreshhold, multiClickThreshhold);
        }

        public void pressedIcon(IdentifiableSupplier<? extends Icon> pressedIcon) {
            attribute(PREFIX + "pressedIcon", AbstractButton::getPressedIcon, AbstractButton::setPressedIcon, pressedIcon);
        }

        public void rolloverEnabled(IdentifiableSupplier<Boolean> rolloverEnabled) {
            attribute(PREFIX + "rolloverEnabled", AbstractButton::isRolloverEnabled, AbstractButton::setRolloverEnabled, rolloverEnabled);
        }

        public void rolloverIcon(IdentifiableSupplier<? extends Icon> rolloverIcon) {
            attribute(PREFIX + "rolloverIcon", AbstractButton::getRolloverIcon, AbstractButton::setRolloverIcon, rolloverIcon);
        }

        public void rolloverSelectedIcon(IdentifiableSupplier<? extends Icon> rolloverSelectedIcon) {
            attribute(PREFIX + "rolloverSelectedIcon", AbstractButton::getRolloverSelectedIcon, AbstractButton::setRolloverSelectedIcon, rolloverSelectedIcon);
        }

        public void selectedIcon(IdentifiableSupplier<? extends Icon> selectedIcon) {
            attribute(PREFIX + "selectedIcon", AbstractButton::getSelectedIcon, AbstractButton::setSelectedIcon, selectedIcon);
        }

        public void verticalAlignment(IdentifiableSupplier<Integer> verticalAlignment) {
            attribute(PREFIX + "verticalAlignment", AbstractButton::getVerticalAlignment, AbstractButton::setVerticalAlignment, verticalAlignment);
        }

        public void verticalTextPosition(IdentifiableSupplier<Integer> verticalTextPosition) {
            attribute(PREFIX + "verticalTextPosition", AbstractButton::getVerticalTextPosition, AbstractButton::setVerticalTextPosition, verticalTextPosition);
        }
    }
}
