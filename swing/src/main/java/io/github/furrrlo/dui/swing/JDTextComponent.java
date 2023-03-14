package io.github.furrrlo.dui.swing;

import javax.swing.text.JTextComponent;
import java.util.Objects;
import java.util.function.Supplier;

public class JDTextComponent {

    public static class Decorator<T extends JTextComponent> extends JDComponent.Decorator<T> {

        private static final String PREFIX = "__JDTextComponent__";

        protected Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);
        }

        public void text(String text) {
            attribute(PREFIX + "text", (textField, t) -> {
                if (!Objects.equals(textField.getText(), t))
                    textField.setText(t);
            }, text);
        }

        public void editable(boolean editable) {
            attribute(PREFIX + "editable", JTextComponent::setEditable, editable);
        }
    }
}
