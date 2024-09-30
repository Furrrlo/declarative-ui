package io.github.furrrlo.dui.swing.text;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.IdentityFreeConsumer;
import io.github.furrrlo.dui.IdentityFreeSupplier;
import io.github.furrrlo.dui.swing.JDComponent;

import javax.swing.*;
import javax.swing.plaf.TextUI;
import javax.swing.text.*;
import java.awt.*;
import java.util.Objects;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDTextComponent {
  public static class Decorator<T extends JTextComponent> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__JDTextComponent__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void ui(IdentityFreeSupplier<? extends TextUI> ui) {
      attribute(PREFIX + "ui", JTextComponent::getUI, JTextComponent::setUI, ui);
    }

    public void caret(IdentityFreeSupplier<? extends Caret> caret) {
      attribute(PREFIX + "caret", JTextComponent::getCaret, JTextComponent::setCaret, caret);
    }

    public void caretColor(IdentityFreeSupplier<? extends Color> caretColor) {
      attribute(PREFIX + "caretColor", JTextComponent::getCaretColor, JTextComponent::setCaretColor, caretColor);
    }

    public void caretPosition(IdentityFreeSupplier<Integer> caretPosition) {
      attribute(PREFIX + "caretPosition", JTextComponent::getCaretPosition, JTextComponent::setCaretPosition, caretPosition);
    }

    public void disabledTextColor(IdentityFreeSupplier<? extends Color> disabledTextColor) {
      attribute(PREFIX + "disabledTextColor", JTextComponent::getDisabledTextColor, JTextComponent::setDisabledTextColor, disabledTextColor);
    }

    public void document(IdentityFreeSupplier<? extends Document> document) {
      attribute(PREFIX + "document", JTextComponent::getDocument, JTextComponent::setDocument, document);
    }

    public void document(IdentityFreeConsumer<DDocument.Decorator<Document>> body) {
      inner(JTextComponent::getDocument, DDocument.forInner(body));
    }

    public void dragEnabled(IdentityFreeSupplier<Boolean> dragEnabled) {
      attribute(PREFIX + "dragEnabled", JTextComponent::getDragEnabled, JTextComponent::setDragEnabled, dragEnabled);
    }

    public void dropMode(IdentityFreeSupplier<DropMode> dropMode) {
      attribute(PREFIX + "dropMode", JTextComponent::getDropMode, JTextComponent::setDropMode, dropMode);
    }

    public void editable(IdentityFreeSupplier<Boolean> editable) {
      attribute(PREFIX + "editable", JTextComponent::isEditable, JTextComponent::setEditable, editable);
    }

    public void focusAccelerator(IdentityFreeSupplier<Character> focusAccelerator) {
      attribute(PREFIX + "focusAccelerator", JTextComponent::getFocusAccelerator, JTextComponent::setFocusAccelerator, focusAccelerator);
    }

    public void highlighter(IdentityFreeSupplier<? extends Highlighter> highlighter) {
      attribute(PREFIX + "highlighter", JTextComponent::getHighlighter, JTextComponent::setHighlighter, highlighter);
    }

    public void keymap(IdentityFreeSupplier<? extends Keymap> keymap) {
      attribute(PREFIX + "keymap", JTextComponent::getKeymap, JTextComponent::setKeymap, keymap);
    }

    public void margin(IdentityFreeSupplier<? extends Insets> margin) {
      attribute(PREFIX + "margin", JTextComponent::getMargin, JTextComponent::setMargin, margin);
    }

    public void navigationFilter(IdentityFreeSupplier<? extends NavigationFilter> navigationFilter) {
      attribute(PREFIX + "navigationFilter", JTextComponent::getNavigationFilter, JTextComponent::setNavigationFilter, navigationFilter);
    }

    public void selectedTextColor(IdentityFreeSupplier<? extends Color> selectedTextColor) {
      attribute(PREFIX + "selectedTextColor", JTextComponent::getSelectedTextColor, JTextComponent::setSelectedTextColor, selectedTextColor);
    }

    public void selectionColor(IdentityFreeSupplier<? extends Color> selectionColor) {
      attribute(PREFIX + "selectionColor", JTextComponent::getSelectionColor, JTextComponent::setSelectionColor, selectionColor);
    }

    public void selectionEnd(IdentityFreeSupplier<Integer> selectionEnd) {
      attribute(PREFIX + "selectionEnd", JTextComponent::getSelectionEnd, JTextComponent::setSelectionEnd, selectionEnd);
    }

    public void selectionStart(IdentityFreeSupplier<Integer> selectionStart) {
      attribute(PREFIX + "selectionStart", JTextComponent::getSelectionStart, JTextComponent::setSelectionStart, selectionStart);
    }
    public void text(IdentityFreeSupplier<String> text) {
      attribute(PREFIX + "text", JTextComponent::setText, text,
              (textField, oldV, newV) -> Objects.equals(textField.getText(), newV));
    }
  }
}
