package io.github.furrrlo.dui.swing;

import javax.swing.text.JTextComponent;
import java.util.Objects;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDTextComponent {

    public static class Decorator<T extends JTextComponent> extends JDComponent.Decorator<T> {

        private static final String PREFIX = "__JDTextComponent__";

        protected Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);
        }

        public void text(Supplier<String> text) {
            attribute(PREFIX + "text", (textField, t) -> {
                if (!Objects.equals(textField.getText(), t))
                    textField.setText(t);
            }, text);
        }

        public void editable(Supplier<Boolean> editable) {
            attribute(PREFIX + "editable", JTextComponent::setEditable, editable);
        }
    }
}
