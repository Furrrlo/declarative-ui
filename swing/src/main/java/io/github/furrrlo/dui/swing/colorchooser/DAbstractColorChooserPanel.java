package io.github.furrrlo.dui.swing.colorchooser;

import io.github.furrrlo.dui.swing.JDPanel;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.function.Supplier;
import javax.swing.colorchooser.AbstractColorChooserPanel;

@SuppressWarnings("unused")
public class DAbstractColorChooserPanel {
  public static class Decorator<T extends AbstractColorChooserPanel> extends JDPanel.Decorator<T> {
    private static final String PREFIX = "__DAbstractColorChooserPanel__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void colorTransparencySelectionEnabled(
        Supplier<Boolean> colorTransparencySelectionEnabled) {
      attribute(PREFIX + "colorTransparencySelectionEnabled", AbstractColorChooserPanel::isColorTransparencySelectionEnabled, AbstractColorChooserPanel::setColorTransparencySelectionEnabled, colorTransparencySelectionEnabled);
    }
  }
}
