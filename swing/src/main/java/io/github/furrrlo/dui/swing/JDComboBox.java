package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentityFreeConsumer;
import io.github.furrrlo.dui.IdentityFreeSupplier;
import io.leangen.geantyref.TypeFactory;
import io.leangen.geantyref.TypeToken;

import javax.swing.*;
import javax.swing.plaf.ComboBoxUI;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDComboBox {

    public static DeclarativeComponent<JComboBox<String>> fn(IdentityFreeConsumer<Decorator<String, JComboBox<String>>> body) {
        return fn(String.class, body);
    }

    public static <E> DeclarativeComponent<JComboBox<E>> fn(Class<E> type,
                                                            IdentityFreeConsumer<Decorator<E, JComboBox<E>>> body) {
        return fn(TypeToken.get(type), body);
    }

    @SuppressWarnings("unchecked")
    public static <E> DeclarativeComponent<JComboBox<E>> fn(TypeToken<E> type,
                                                            IdentityFreeConsumer<Decorator<E, JComboBox<E>>> body) {
        return fn(
                (TypeToken<JComboBox<E>>) TypeToken.get(TypeFactory.parameterizedClass(JComboBox.class, type.getType())),
                JComboBox::new,
                body);
    }

    public static <E, T extends JComboBox<E>> DeclarativeComponent<T> fn(TypeToken<T> type,
                                                                         Supplier<T> factory,
                                                                         IdentityFreeConsumer<Decorator<E, T>> body) {
        return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
    }

    public static class Decorator<E, T extends JComboBox<E>> extends JDComponent.Decorator<T> {

        private static final String PREFIX = "__JDComboBox__";

        protected Decorator(TypeToken<T> type, Supplier<T> factory) {
            super(type, factory);
        }

        protected TypeToken<E> getLiteralTypeArg() {
            return getLiteralTypeArgumentAt(0);
        }

        public void items(IdentityFreeSupplier<List<E>> items) {
            listAttribute(
                    PREFIX + "items",
                    getLiteralTypeArg(),
                    JComboBox::removeItemAt,
                    items, (comboBox, idx, s, v) -> {
                        if (idx >= comboBox.getItemCount())
                            comboBox.addItem(v);
                        else
                            comboBox.insertItemAt(v, idx);
                    });
        }

        public void selectedItem(IdentityFreeSupplier<Object> item) {
            attribute(PREFIX + "selectedItem", JComboBox::getSelectedItem, JComboBox::setSelectedItem, item);
        }

        public void actionListener(ActionListener l) {
            eventListener(
                    PREFIX + "actionListener",
                    ActionListener.class,
                    ActionListenerWrapper::new,
                    JComboBox::addActionListener,
                    l);
        }

        public void ui(IdentityFreeSupplier<? extends ComboBoxUI> ui) {
            attribute(PREFIX + "ui", JComboBox::getUI, JComboBox::setUI, ui);
        }

        public void action(IdentityFreeSupplier<? extends Action> action) {
            attribute(PREFIX + "action", JComboBox::getAction, JComboBox::setAction, action);
        }

        public void actionCommand(IdentityFreeSupplier<String> actionCommand) {
            attribute(PREFIX + "actionCommand", JComboBox::getActionCommand, JComboBox::setActionCommand, actionCommand);
        }

        public void editable(IdentityFreeSupplier<Boolean> editable) {
            attribute(PREFIX + "editable", JComboBox::isEditable, JComboBox::setEditable, editable);
        }

        public void editor(IdentityFreeSupplier<? extends ComboBoxEditor> editor) {
            attribute(PREFIX + "editor", JComboBox::getEditor, JComboBox::setEditor, editor);
        }

        public void keySelectionManager(IdentityFreeSupplier<? extends JComboBox.KeySelectionManager> keySelectionManager) {
            attribute(PREFIX + "keySelectionManager", JComboBox::getKeySelectionManager, JComboBox::setKeySelectionManager, keySelectionManager);
        }

        public void lightWeightPopupEnabled(IdentityFreeSupplier<Boolean> lightWeightPopupEnabled) {
            attribute(PREFIX + "lightWeightPopupEnabled", JComboBox::isLightWeightPopupEnabled, JComboBox::setLightWeightPopupEnabled, lightWeightPopupEnabled);
        }

        public void maximumRowCount(IdentityFreeSupplier<Integer> maximumRowCount) {
            attribute(PREFIX + "maximumRowCount", JComboBox::getMaximumRowCount, JComboBox::setMaximumRowCount, maximumRowCount);
        }

        public void model(IdentityFreeSupplier<? extends ComboBoxModel<E>> model) {
            attribute(PREFIX + "model", JComboBox::getModel, JComboBox::setModel, model);
        }

        public void popupVisible(IdentityFreeSupplier<Boolean> popupVisible) {
            attribute(PREFIX + "popupVisible", JComboBox::isPopupVisible, JComboBox::setPopupVisible, popupVisible);
        }

        public void prototypeDisplayValue(IdentityFreeSupplier<E> prototypeDisplayValue) {
            attribute(PREFIX + "prototypeDisplayValue", JComboBox::getPrototypeDisplayValue, JComboBox::setPrototypeDisplayValue, prototypeDisplayValue);
        }

        public void renderer(IdentityFreeSupplier<? extends ListCellRenderer<? super E>> renderer) {
            this.<ListCellRenderer<? super E>>attribute(PREFIX + "renderer", JComboBox::getRenderer, JComboBox::setRenderer, renderer);
        }

        public void selectedIndex(IdentityFreeSupplier<Integer> selectedIndex) {
            attribute(PREFIX + "selectedIndex", JComboBox::getSelectedIndex, JComboBox::setSelectedIndex, selectedIndex);
        }
    }
}
