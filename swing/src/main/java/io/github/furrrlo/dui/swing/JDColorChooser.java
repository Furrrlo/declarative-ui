package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.*;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.plaf.ColorChooserUI;
import java.awt.*;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDColorChooser {
  public static DeclarativeComponent<JColorChooser> fn(
      IdentityFreeConsumer<Decorator<JColorChooser>> body) {
    return fn(JColorChooser.class, JColorChooser::new, body);
  }

  public static DeclarativeComponent<JColorChooser> fn(Supplier<JColorChooser> factory,
      IdentityFreeConsumer<Decorator<JColorChooser>> body) {
    return fn(JColorChooser.class, factory, body);
  }

  public static <T extends JColorChooser> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentityFreeConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JColorChooser> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__JDColorChooser__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void ui(IdentityFreeSupplier<? extends ColorChooserUI> ui) {
      attribute(PREFIX + "ui", JColorChooser::getUI, JColorChooser::setUI, ui);
    }

    public void chooserPanels() {
      // TODO: implement "chooserPanels"
    }

    public void color(IdentityFreeSupplier<? extends Color> color) {
      attribute(PREFIX + "color", JColorChooser::getColor, JColorChooser::setColor, color);
    }

    public void dragEnabled(IdentityFreeSupplier<Boolean> dragEnabled) {
      attribute(PREFIX + "dragEnabled", JColorChooser::getDragEnabled, JColorChooser::setDragEnabled, dragEnabled);
    }

    public void previewPanel(
        @Nullable DeclarativeComponentSupplier<? extends JComponent> previewPanel) {
      fnAttribute(PREFIX + "previewPanel", JColorChooser::getPreviewPanel, JColorChooser::setPreviewPanel, previewPanel);
    }

    public void selectionModel(IdentityFreeSupplier<? extends ColorSelectionModel> selectionModel) {
      attribute(PREFIX + "selectionModel", JColorChooser::getSelectionModel, JColorChooser::setSelectionModel, selectionModel);
    }
  }
}
