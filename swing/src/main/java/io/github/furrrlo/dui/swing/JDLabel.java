package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.*;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import javax.swing.plaf.LabelUI;
import java.awt.*;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDLabel {

    public static DeclarativeComponent<JLabel> fn(IdentityFreeConsumer<Decorator<JLabel>> body) {
        return fn(JLabel.class, JLabel::new, body);
    }

    public static DeclarativeComponent<JLabel> fn(Supplier<JLabel> factory,
                                                  IdentityFreeConsumer<Decorator<JLabel>> body) {
        return fn(JLabel.class, factory, body);
    }

    public static <T extends JLabel> DeclarativeComponent<T> fn(Class<T> type,
                                                                Supplier<T> factory,
                                                                IdentityFreeConsumer<Decorator<T>> body) {
        return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
    }

    public static class Decorator<T extends JLabel> extends JDComponent.Decorator<T> {

        private static final String PREFIX = "__JDLabel__";

        protected Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);
        }

        public void text(IdentityFreeSupplier<String> text) {
            attribute(PREFIX + "text", JLabel::getText, JLabel::setText, text);
        }

        public void icon(IdentityFreeSupplier<? extends Icon> icon) {
            attribute(PREFIX + "icon", JLabel::getIcon, JLabel::setIcon, icon);
        }

        public void ui(IdentityFreeSupplier<? extends LabelUI> ui) {
            attribute(PREFIX + "ui", JLabel::getUI, JLabel::setUI, ui);
        }

        public void disabledIcon(IdentityFreeSupplier<? extends Icon> disabledIcon) {
            attribute(PREFIX + "disabledIcon", JLabel::getDisabledIcon, JLabel::setDisabledIcon, disabledIcon);
        }

        public void displayedMnemonic(IdentityFreeSupplier<Integer> displayedMnemonic) {
            attribute(PREFIX + "displayedMnemonic", JLabel::getDisplayedMnemonic, JLabel::setDisplayedMnemonic, displayedMnemonic);
        }

        public void displayedMnemonicIndex(IdentityFreeSupplier<Integer> displayedMnemonicIndex) {
            attribute(PREFIX + "displayedMnemonicIndex", JLabel::getDisplayedMnemonicIndex, JLabel::setDisplayedMnemonicIndex, displayedMnemonicIndex);
        }

        public void horizontalAlignment(IdentityFreeSupplier<Integer> horizontalAlignment) {
            attribute(PREFIX + "horizontalAlignment", JLabel::getHorizontalAlignment, JLabel::setHorizontalAlignment, horizontalAlignment);
        }

        public void horizontalTextPosition(IdentityFreeSupplier<Integer> horizontalTextPosition) {
            attribute(PREFIX + "horizontalTextPosition", JLabel::getHorizontalTextPosition, JLabel::setHorizontalTextPosition, horizontalTextPosition);
        }

        public void iconTextGap(IdentityFreeSupplier<Integer> iconTextGap) {
            attribute(PREFIX + "iconTextGap", JLabel::getIconTextGap, JLabel::setIconTextGap, iconTextGap);
        }

        public void labelFor(@Nullable DeclarativeComponentSupplier<? extends Component> labelFor) {
            fnAttribute(PREFIX + "labelFor", JLabel::getLabelFor, JLabel::setLabelFor, labelFor);
        }

        public void verticalAlignment(IdentityFreeSupplier<Integer> verticalAlignment) {
            attribute(PREFIX + "verticalAlignment", JLabel::getVerticalAlignment, JLabel::setVerticalAlignment, verticalAlignment);
        }

        public void verticalTextPosition(IdentityFreeSupplier<Integer> verticalTextPosition) {
            attribute(PREFIX + "verticalTextPosition", JLabel::getVerticalTextPosition, JLabel::setVerticalTextPosition, verticalTextPosition);
        }
    }
}
