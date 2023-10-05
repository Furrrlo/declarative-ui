package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.Supplier;

public class JDComboBox {


    public static DeclarativeComponent<JComboBox<String>> fn(
            DeclarativeComponent.Body<JComboBox<String>, Decorator<JComboBox<String>>> body) {
        return fn(JComboBox::new, body);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static DeclarativeComponent<JComboBox<String>> fn(Supplier<JComboBox<String>> factory,
                                                             DeclarativeComponent.Body<JComboBox<String>, Decorator<JComboBox<String>>> body) {
        return fn((Class<JComboBox<String>>) (Class) JComboBox.class, factory, body);
    }

    public static <T extends JComboBox<String>> DeclarativeComponent<T> fn(Class<T> type,
                                                                           Supplier<T> factory,
                                                                           DeclarativeComponent.Body<T, Decorator<T>> body) {
        return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
    }

    public static class Decorator<T extends JComboBox<String>> extends JDComponent.Decorator<T> {

        private static final String PREFIX = "__JDComboBox__";

        protected Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);
        }

        public void items(List<String> items) {
            listAttribute(
                    PREFIX + "items",
                    String.class,
                    JComboBox::removeItemAt,
                    items, (comboBox, idx, s, v) -> {
                        if (idx >= comboBox.getItemCount())
                            comboBox.addItem(v);
                        else
                            comboBox.insertItemAt(v, idx);
                    });
        }

        public void selectedItem(Supplier<String> item) {
            attribute(PREFIX + "selectedItem", JComboBox::setSelectedItem, item);
        }

        public void actionListener(ActionListener l) {
            eventListener(
                    PREFIX + "actionListener",
                    ActionListener.class,
                    ActionListenerWrapper::new,
                    JComboBox::addActionListener,
                    l);
        }
    }
}
