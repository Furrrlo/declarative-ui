package io.github.furrrlo.dui.swing.text;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public interface SimpleDocumentListener extends DocumentListener {

    @Override
    default void insertUpdate(DocumentEvent e) {
        update(e);
    }

    @Override

    default void removeUpdate(DocumentEvent e) {
        update(e);
    }

    @Override
    default void changedUpdate(DocumentEvent e) {
        update(e);
    }

    void update(DocumentEvent e);
}
