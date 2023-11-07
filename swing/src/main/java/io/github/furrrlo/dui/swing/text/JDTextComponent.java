package io.github.furrrlo.dui.swing.text;

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

    public void ui(Supplier<? extends TextUI> ui) {
      attribute(PREFIX + "ui", JTextComponent::getUI, JTextComponent::setUI, ui);
    }

    public void caret(Supplier<? extends Caret> caret) {
      attribute(PREFIX + "caret", JTextComponent::getCaret, JTextComponent::setCaret, caret);
    }

    public void caretColor(Supplier<? extends Color> caretColor) {
      attribute(PREFIX + "caretColor", JTextComponent::getCaretColor, JTextComponent::setCaretColor, caretColor);
    }

    public void caretPosition(Supplier<Integer> caretPosition) {
      attribute(PREFIX + "caretPosition", JTextComponent::getCaretPosition, JTextComponent::setCaretPosition, caretPosition);
    }

    public void componentOrientation(Supplier<ComponentOrientation> componentOrientation) {
      attribute(PREFIX + "componentOrientation", JTextComponent::setComponentOrientation, componentOrientation);
    }

    public void disabledTextColor(Supplier<? extends Color> disabledTextColor) {
      attribute(PREFIX + "disabledTextColor", JTextComponent::getDisabledTextColor, JTextComponent::setDisabledTextColor, disabledTextColor);
    }

    public void document(Supplier<? extends Document> document) {
      attribute(PREFIX + "document", JTextComponent::getDocument, JTextComponent::setDocument, document);
    }

    public void dragEnabled(Supplier<Boolean> dragEnabled) {
      attribute(PREFIX + "dragEnabled", JTextComponent::getDragEnabled, JTextComponent::setDragEnabled, dragEnabled);
    }

    public void dropMode(Supplier<DropMode> dropMode) {
      attribute(PREFIX + "dropMode", JTextComponent::getDropMode, JTextComponent::setDropMode, dropMode);
    }

    public void editable(Supplier<Boolean> editable) {
      attribute(PREFIX + "editable", JTextComponent::isEditable, JTextComponent::setEditable, editable);
    }

    public void focusAccelerator(Supplier<Character> focusAccelerator) {
      attribute(PREFIX + "focusAccelerator", JTextComponent::getFocusAccelerator, JTextComponent::setFocusAccelerator, focusAccelerator);
    }

    public void highlighter(Supplier<? extends Highlighter> highlighter) {
      attribute(PREFIX + "highlighter", JTextComponent::getHighlighter, JTextComponent::setHighlighter, highlighter);
    }

    public void keymap(Supplier<? extends Keymap> keymap) {
      attribute(PREFIX + "keymap", JTextComponent::getKeymap, JTextComponent::setKeymap, keymap);
    }

    public void margin(Supplier<? extends Insets> margin) {
      attribute(PREFIX + "margin", JTextComponent::getMargin, JTextComponent::setMargin, margin);
    }

    public void navigationFilter(Supplier<? extends NavigationFilter> navigationFilter) {
      attribute(PREFIX + "navigationFilter", JTextComponent::getNavigationFilter, JTextComponent::setNavigationFilter, navigationFilter);
    }

    public void selectedTextColor(Supplier<? extends Color> selectedTextColor) {
      attribute(PREFIX + "selectedTextColor", JTextComponent::getSelectedTextColor, JTextComponent::setSelectedTextColor, selectedTextColor);
    }

    public void selectionColor(Supplier<? extends Color> selectionColor) {
      attribute(PREFIX + "selectionColor", JTextComponent::getSelectionColor, JTextComponent::setSelectionColor, selectionColor);
    }

    public void selectionEnd(Supplier<Integer> selectionEnd) {
      attribute(PREFIX + "selectionEnd", JTextComponent::getSelectionEnd, JTextComponent::setSelectionEnd, selectionEnd);
    }

    public void selectionStart(Supplier<Integer> selectionStart) {
      attribute(PREFIX + "selectionStart", JTextComponent::getSelectionStart, JTextComponent::setSelectionStart, selectionStart);
    }
    public void text(Supplier<String> text) {
      attribute(PREFIX + "text", JTextComponent::getText, (textField, t) -> {
        if (!Objects.equals(textField.getText(), t))
          textField.setText(t);
      }, text);
    }
  }
}
