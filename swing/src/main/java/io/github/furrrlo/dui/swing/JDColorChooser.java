package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.DeclarativeComponentSupplier;
import io.github.furrrlo.dui.IdentifiableConsumer;
import java.awt.Color;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.function.Supplier;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.plaf.ColorChooserUI;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class JDColorChooser {
  public static DeclarativeComponent<JColorChooser> fn(
      IdentifiableConsumer<Decorator<JColorChooser>> body) {
    return fn(JColorChooser.class, JColorChooser::new, body);
  }

  public static DeclarativeComponent<JColorChooser> fn(Supplier<JColorChooser> factory,
      IdentifiableConsumer<Decorator<JColorChooser>> body) {
    return fn(JColorChooser.class, factory, body);
  }

  public static <T extends JColorChooser> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentifiableConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JColorChooser> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__JDColorChooser__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void ui(Supplier<? extends ColorChooserUI> ui) {
      attribute(PREFIX + "ui", JColorChooser::getUI, JColorChooser::setUI, ui);
    }

    public void chooserPanels() {
      // TODO: implement "chooserPanels"
    }

    public void color(Supplier<? extends Color> color) {
      attribute(PREFIX + "color", JColorChooser::getColor, JColorChooser::setColor, color);
    }

    public void dragEnabled(Supplier<Boolean> dragEnabled) {
      attribute(PREFIX + "dragEnabled", JColorChooser::getDragEnabled, JColorChooser::setDragEnabled, dragEnabled);
    }

    public void previewPanel(
        @Nullable DeclarativeComponentSupplier<? extends JComponent> previewPanel) {
      fnAttribute(PREFIX + "previewPanel", JColorChooser::getPreviewPanel, JColorChooser::setPreviewPanel, previewPanel);
    }

    public void selectionModel(Supplier<? extends ColorSelectionModel> selectionModel) {
      attribute(PREFIX + "selectionModel", JColorChooser::getSelectionModel, JColorChooser::setSelectionModel, selectionModel);
    }
  }
}
