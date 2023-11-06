package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.Supplier;

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
    
public void ui(java.util.function.Supplier<? extends javax.swing.plaf.ComboBoxUI> ui) {
  attribute(PREFIX + "ui", javax.swing.JComboBox::getUI, javax.swing.JComboBox::setUI, ui);
}

public void action(java.util.function.Supplier<? extends javax.swing.Action> action) {
  attribute(PREFIX + "action", javax.swing.JComboBox::getAction, javax.swing.JComboBox::setAction, action);
}

public void actionCommand(java.util.function.Supplier<java.lang.String> actionCommand) {
  attribute(PREFIX + "actionCommand", javax.swing.JComboBox::getActionCommand, javax.swing.JComboBox::setActionCommand, actionCommand);
}

public void editable(java.util.function.Supplier<java.lang.Boolean> editable) {
  attribute(PREFIX + "editable", javax.swing.JComboBox::isEditable, javax.swing.JComboBox::setEditable, editable);
}

public void editor(java.util.function.Supplier<? extends javax.swing.ComboBoxEditor> editor) {
  attribute(PREFIX + "editor", javax.swing.JComboBox::getEditor, javax.swing.JComboBox::setEditor, editor);
}

public void enabled(java.util.function.Supplier<java.lang.Boolean> enabled) {
  attribute(PREFIX + "enabled", javax.swing.JComboBox::setEnabled, enabled);
}

public void keySelectionManager(
    java.util.function.Supplier<? extends javax.swing.JComboBox.KeySelectionManager> keySelectionManager) {
  attribute(PREFIX + "keySelectionManager", javax.swing.JComboBox::getKeySelectionManager, javax.swing.JComboBox::setKeySelectionManager, keySelectionManager);
}

public void lightWeightPopupEnabled(
    java.util.function.Supplier<java.lang.Boolean> lightWeightPopupEnabled) {
  attribute(PREFIX + "lightWeightPopupEnabled", javax.swing.JComboBox::isLightWeightPopupEnabled, javax.swing.JComboBox::setLightWeightPopupEnabled, lightWeightPopupEnabled);
}

public void maximumRowCount(java.util.function.Supplier<java.lang.Integer> maximumRowCount) {
  attribute(PREFIX + "maximumRowCount", javax.swing.JComboBox::getMaximumRowCount, javax.swing.JComboBox::setMaximumRowCount, maximumRowCount);
}

public void model(java.util.function.Supplier<? extends javax.swing.ComboBoxModel> model) {
  attribute(PREFIX + "model", javax.swing.JComboBox::getModel, javax.swing.JComboBox::setModel, model);
}

public void popupVisible(java.util.function.Supplier<java.lang.Boolean> popupVisible) {
  attribute(PREFIX + "popupVisible", javax.swing.JComboBox::isPopupVisible, javax.swing.JComboBox::setPopupVisible, popupVisible);
}

public void prototypeDisplayValue(java.util.function.Supplier<?> prototypeDisplayValue) {
  attribute(PREFIX + "prototypeDisplayValue", javax.swing.JComboBox::getPrototypeDisplayValue, javax.swing.JComboBox::setPrototypeDisplayValue, prototypeDisplayValue);
}

public void renderer(java.util.function.Supplier<? extends javax.swing.ListCellRenderer> renderer) {
  attribute(PREFIX + "renderer", javax.swing.JComboBox::getRenderer, javax.swing.JComboBox::setRenderer, renderer);
}

public void selectedIndex(java.util.function.Supplier<java.lang.Integer> selectedIndex) {
  attribute(PREFIX + "selectedIndex", javax.swing.JComboBox::getSelectedIndex, javax.swing.JComboBox::setSelectedIndex, selectedIndex);
}
}
}
