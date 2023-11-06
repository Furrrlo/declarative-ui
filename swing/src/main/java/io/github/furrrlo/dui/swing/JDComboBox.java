package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;

import javax.swing.*;
import javax.swing.plaf.ComboBoxUI;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDComboBox {


    public static DeclarativeComponent<JComboBox<String>> fn(IdentifiableConsumer<Decorator<JComboBox<String>>> body) {
        return fn(JComboBox::new, body);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static DeclarativeComponent<JComboBox<String>> fn(Supplier<JComboBox<String>> factory,
                                                             IdentifiableConsumer<Decorator<JComboBox<String>>> body) {
        return fn((Class<JComboBox<String>>) (Class) JComboBox.class, factory, body);
    }

    public static <T extends JComboBox<String>> DeclarativeComponent<T> fn(Class<T> type,
                                                                           Supplier<T> factory,
                                                                           IdentifiableConsumer<Decorator<T>> body) {
        return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
    }

    public static class Decorator<T extends JComboBox<String>> extends JDComponent.Decorator<T> {

        private static final String PREFIX = "__JDComboBox__";

        protected Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);
        }

        public void items(Supplier<List<String>> items) {
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

        public void ui(Supplier<? extends ComboBoxUI> ui) {
            attribute(PREFIX + "ui", JComboBox::getUI, JComboBox::setUI, ui);
        }

        public void action(Supplier<? extends Action> action) {
            attribute(PREFIX + "action", JComboBox::getAction, JComboBox::setAction, action);
        }

        public void actionCommand(Supplier<String> actionCommand) {
            attribute(PREFIX + "actionCommand", JComboBox::getActionCommand, JComboBox::setActionCommand, actionCommand);
        }

        public void editable(Supplier<Boolean> editable) {
            attribute(PREFIX + "editable", JComboBox::isEditable, JComboBox::setEditable, editable);
        }

        public void editor(Supplier<? extends ComboBoxEditor> editor) {
            attribute(PREFIX + "editor", JComboBox::getEditor, JComboBox::setEditor, editor);
        }

        public void enabled(Supplier<Boolean> enabled) {
            attribute(PREFIX + "enabled", JComboBox::setEnabled, enabled);
        }

        public void keySelectionManager(
                Supplier<? extends JComboBox.KeySelectionManager> keySelectionManager) {
            attribute(PREFIX + "keySelectionManager", JComboBox::getKeySelectionManager, JComboBox::setKeySelectionManager, keySelectionManager);
        }

        public void lightWeightPopupEnabled(
                Supplier<Boolean> lightWeightPopupEnabled) {
            attribute(PREFIX + "lightWeightPopupEnabled", JComboBox::isLightWeightPopupEnabled, JComboBox::setLightWeightPopupEnabled, lightWeightPopupEnabled);
        }

        public void maximumRowCount(Supplier<Integer> maximumRowCount) {
            attribute(PREFIX + "maximumRowCount", JComboBox::getMaximumRowCount, JComboBox::setMaximumRowCount, maximumRowCount);
        }

        public void model(Supplier<? extends ComboBoxModel<String>> model) {
            attribute(PREFIX + "model", JComboBox::getModel, JComboBox::setModel, model);
        }

        public void popupVisible(Supplier<Boolean> popupVisible) {
            attribute(PREFIX + "popupVisible", JComboBox::isPopupVisible, JComboBox::setPopupVisible, popupVisible);
        }

        public void prototypeDisplayValue(Supplier<String> prototypeDisplayValue) {
            attribute(PREFIX + "prototypeDisplayValue", JComboBox::getPrototypeDisplayValue, JComboBox::setPrototypeDisplayValue, prototypeDisplayValue);
        }

        public void renderer(Supplier<? extends ListCellRenderer<? super String>> renderer) {
            this.<ListCellRenderer<? super String>>attribute(PREFIX + "renderer", JComboBox::getRenderer, JComboBox::setRenderer, renderer);
        }

        public void selectedIndex(Supplier<Integer> selectedIndex) {
            attribute(PREFIX + "selectedIndex", JComboBox::getSelectedIndex, JComboBox::setSelectedIndex, selectedIndex);
        }
    }
}
