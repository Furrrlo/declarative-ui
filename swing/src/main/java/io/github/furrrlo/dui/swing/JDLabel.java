package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.DeclarativeComponentSupplier;
import io.github.furrrlo.dui.IdentifiableConsumer;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.plaf.LabelUI;
import java.awt.Component;
import java.util.function.Supplier;

@SuppressWarnings("unused")
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
            attribute(PREFIX + "text", JLabel::getText, JLabel::setText, text);
        }

        public void icon(Supplier<? extends Icon> icon) {
            attribute(PREFIX + "icon", JLabel::getIcon, JLabel::setIcon, icon);
        }

        public void ui(Supplier<? extends LabelUI> ui) {
            attribute(PREFIX + "ui", JLabel::getUI, JLabel::setUI, ui);
        }

        public void disabledIcon(Supplier<? extends Icon> disabledIcon) {
            attribute(PREFIX + "disabledIcon", JLabel::getDisabledIcon, JLabel::setDisabledIcon, disabledIcon);
        }

        public void displayedMnemonic(Supplier<Integer> displayedMnemonic) {
            attribute(PREFIX + "displayedMnemonic", JLabel::getDisplayedMnemonic, JLabel::setDisplayedMnemonic, displayedMnemonic);
        }

        public void displayedMnemonicIndex(Supplier<Integer> displayedMnemonicIndex) {
            attribute(PREFIX + "displayedMnemonicIndex", JLabel::getDisplayedMnemonicIndex, JLabel::setDisplayedMnemonicIndex, displayedMnemonicIndex);
        }

        public void horizontalAlignment(Supplier<Integer> horizontalAlignment) {
            attribute(PREFIX + "horizontalAlignment", JLabel::getHorizontalAlignment, JLabel::setHorizontalAlignment, horizontalAlignment);
        }

        public void horizontalTextPosition(Supplier<Integer> horizontalTextPosition) {
            attribute(PREFIX + "horizontalTextPosition", JLabel::getHorizontalTextPosition, JLabel::setHorizontalTextPosition, horizontalTextPosition);
        }

        public void iconTextGap(Supplier<Integer> iconTextGap) {
            attribute(PREFIX + "iconTextGap", JLabel::getIconTextGap, JLabel::setIconTextGap, iconTextGap);
        }

        public void labelFor(@Nullable DeclarativeComponentSupplier<? extends Component> labelFor) {
            fnAttribute(PREFIX + "labelFor", JLabel::getLabelFor, JLabel::setLabelFor, labelFor);
        }

        public void verticalAlignment(Supplier<Integer> verticalAlignment) {
            attribute(PREFIX + "verticalAlignment", JLabel::getVerticalAlignment, JLabel::setVerticalAlignment, verticalAlignment);
        }

        public void verticalTextPosition(Supplier<Integer> verticalTextPosition) {
            attribute(PREFIX + "verticalTextPosition", JLabel::getVerticalTextPosition, JLabel::setVerticalTextPosition, verticalTextPosition);
        }
    }
}
