package io.github.furrrlo.dui.swing.text;

import org.jspecify.annotations.Nullable;

import java.util.EventObject;

public class TextChangeEvent extends EventObject {

    private final @Nullable String prevText;
    private final @Nullable String newText;

    public TextChangeEvent(Object source, @Nullable String prevText, @Nullable String newText) {
        super(source);
        this.prevText = prevText;
        this.newText = newText;
    }

    public @Nullable String getPrevText() {
        return prevText;
    }

    public String getPrevTextOr(String def) {
        return prevText != null ? prevText : def;
    }

    public @Nullable String getNewText() {
        return newText;
    }

    public String getNewTextOr(String def) {
        return newText != null ? newText : def;
    }

    @Override
    public String toString() {
        return "TextChangeEvent[" +
                "prevText='" + prevText + '\'' +
                ", newText='" + newText + '\'' +
                "] " + super.toString();
    }
}
