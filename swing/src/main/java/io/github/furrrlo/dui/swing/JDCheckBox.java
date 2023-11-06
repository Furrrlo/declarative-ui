package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;

import javax.swing.*;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDCheckBox {

    public static DeclarativeComponent<JCheckBox> fn(IdentifiableConsumer<Decorator<JCheckBox>> body) {
        return fn(JCheckBox.class, JCheckBox::new, body);
    }

    public static DeclarativeComponent<JCheckBox> fn(Supplier<JCheckBox> factory,
                                                     IdentifiableConsumer<Decorator<JCheckBox>> body) {
        return fn(JCheckBox.class, factory, body);
    }

    public static <T extends JCheckBox> DeclarativeComponent<T> fn(Class<T> type,
                                                                   Supplier<T> factory,
                                                                   IdentifiableConsumer<Decorator<T>> body) {
        return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
    }

    public static class Decorator<T extends JCheckBox> extends DAbstractButton.Decorator<T> {

        private static final String PREFIX = "__JDCheckBox__";

        protected Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);
        }

        public void borderPaintedFlat(Supplier<Boolean> borderPaintedFlat) {
            attribute(PREFIX + "borderPaintedFlat", JCheckBox::isBorderPaintedFlat, JCheckBox::setBorderPaintedFlat, borderPaintedFlat);
        }
    }
}
