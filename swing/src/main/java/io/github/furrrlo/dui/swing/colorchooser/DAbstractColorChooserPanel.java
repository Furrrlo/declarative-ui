package io.github.furrrlo.dui.swing.colorchooser;

import io.github.furrrlo.dui.IdentityFreeSupplier;
import io.github.furrrlo.dui.swing.JDPanel;

import javax.swing.colorchooser.AbstractColorChooserPanel;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class DAbstractColorChooserPanel {
  public static class Decorator<T extends AbstractColorChooserPanel> extends JDPanel.Decorator<T> {
    private static final String PREFIX = "__DAbstractColorChooserPanel__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void colorTransparencySelectionEnabled(IdentityFreeSupplier<Boolean> colorTransparencySelectionEnabled) {
      // TODO: this was added in Java 9 apparently
//      attribute(PREFIX + "colorTransparencySelectionEnabled", AbstractColorChooserPanel::isColorTransparencySelectionEnabled, AbstractColorChooserPanel::setColorTransparencySelectionEnabled, colorTransparencySelectionEnabled);
    }
  }
}
