package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;

import javax.swing.*;
import java.util.function.Supplier;

public class JDLabel {

    public static DeclarativeComponent<JLabel> fn(IdentifiableConsumer<Decorator<JLabel>> body) {
        return fn(JLabel.class, JLabel::new, body);
    }

    public static DeclarativeComponent<JLabel> fn(Supplier<JLabel> factory,
                                                  IdentifiableConsumer<Decorator<JLabel>> body) {
        return fn(JLabel.class, factory, body);
    }

    public static <T extends JLabel> DeclarativeComponent<T> fn(Class<T> type,
                                                                Supplier<T> factory,
                                                                IdentifiableConsumer<Decorator<T>> body) {
        return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
    }

    public static class Decorator<T extends JLabel> extends JDComponent.Decorator<T> {

        private static final String PREFIX = "__JDLabel__";

        protected Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);
        }

        public void text(Supplier<String> text) {
            attribute(PREFIX + "text", JLabel::setText, text);
        }

        public void icon(Supplier<? extends Icon> icon) {
            attribute(PREFIX + "icon", JLabel::setIcon, icon);
        }

        public void enabled(Supplier<Boolean> enabled) {
            attribute(PREFIX + "enabled", JLabel::setEnabled, enabled);
        }
    
public void ui(java.util.function.Supplier<? extends javax.swing.plaf.LabelUI> ui) {
  attribute(PREFIX + "ui", javax.swing.JLabel::getUI, javax.swing.JLabel::setUI, ui);
}

public void disabledIcon(java.util.function.Supplier<? extends javax.swing.Icon> disabledIcon) {
  attribute(PREFIX + "disabledIcon", javax.swing.JLabel::getDisabledIcon, javax.swing.JLabel::setDisabledIcon, disabledIcon);
}

public void displayedMnemonic(java.util.function.Supplier<java.lang.Integer> displayedMnemonic) {
  attribute(PREFIX + "displayedMnemonic", javax.swing.JLabel::getDisplayedMnemonic, javax.swing.JLabel::setDisplayedMnemonic, displayedMnemonic);
}

public void displayedMnemonicIndex(
    java.util.function.Supplier<java.lang.Integer> displayedMnemonicIndex) {
  attribute(PREFIX + "displayedMnemonicIndex", javax.swing.JLabel::getDisplayedMnemonicIndex, javax.swing.JLabel::setDisplayedMnemonicIndex, displayedMnemonicIndex);
}

public void horizontalAlignment(
    java.util.function.Supplier<java.lang.Integer> horizontalAlignment) {
  attribute(PREFIX + "horizontalAlignment", javax.swing.JLabel::getHorizontalAlignment, javax.swing.JLabel::setHorizontalAlignment, horizontalAlignment);
}

public void horizontalTextPosition(
    java.util.function.Supplier<java.lang.Integer> horizontalTextPosition) {
  attribute(PREFIX + "horizontalTextPosition", javax.swing.JLabel::getHorizontalTextPosition, javax.swing.JLabel::setHorizontalTextPosition, horizontalTextPosition);
}

public void iconTextGap(java.util.function.Supplier<java.lang.Integer> iconTextGap) {
  attribute(PREFIX + "iconTextGap", javax.swing.JLabel::getIconTextGap, javax.swing.JLabel::setIconTextGap, iconTextGap);
}

public void labelFor(
    io.github.furrrlo.dui. @org.jetbrains.annotations.Nullable DeclarativeComponentSupplier<? extends java.awt.Component> labelFor) {
  fnAttribute(PREFIX + "labelFor", javax.swing.JLabel::getLabelFor, javax.swing.JLabel::setLabelFor, labelFor);
}

public void verticalAlignment(java.util.function.Supplier<java.lang.Integer> verticalAlignment) {
  attribute(PREFIX + "verticalAlignment", javax.swing.JLabel::getVerticalAlignment, javax.swing.JLabel::setVerticalAlignment, verticalAlignment);
}

public void verticalTextPosition(
    java.util.function.Supplier<java.lang.Integer> verticalTextPosition) {
  attribute(PREFIX + "verticalTextPosition", javax.swing.JLabel::getVerticalTextPosition, javax.swing.JLabel::setVerticalTextPosition, verticalTextPosition);
}
}
}
