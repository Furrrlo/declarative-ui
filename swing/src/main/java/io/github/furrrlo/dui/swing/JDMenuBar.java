package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.*;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.plaf.MenuBarUI;
import java.awt.*;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDMenuBar {
  public static DeclarativeComponent<JMenuBar> fn(IdentifiableConsumer<Decorator<JMenuBar>> body) {
    return fn(JMenuBar.class, JMenuBar::new, body);
  }

  public static DeclarativeComponent<JMenuBar> fn(Supplier<JMenuBar> factory,
      IdentifiableConsumer<Decorator<JMenuBar>> body) {
    return fn(JMenuBar.class, factory, body);
  }

  public static <T extends JMenuBar> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentifiableConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JMenuBar> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__JDMenuBar__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void ui(IdentifiableSupplier<? extends MenuBarUI> ui) {
      attribute(PREFIX + "ui", JMenuBar::getUI, JMenuBar::setUI, ui);
    }

    public void borderPainted(IdentifiableSupplier<Boolean> borderPainted) {
      attribute(PREFIX + "borderPainted", JMenuBar::isBorderPainted, JMenuBar::setBorderPainted, borderPainted);
    }

    public void helpMenu(@Nullable DeclarativeComponentSupplier<? extends JMenu> helpMenu) {
      fnAttribute(PREFIX + "helpMenu", JMenuBar::getHelpMenu, JMenuBar::setHelpMenu, helpMenu);
    }

    public void margin(IdentifiableSupplier<? extends Insets> margin) {
      attribute(PREFIX + "margin", JMenuBar::getMargin, JMenuBar::setMargin, margin);
    }

    public void selectionModel(IdentifiableSupplier<? extends SingleSelectionModel> selectionModel) {
      attribute(PREFIX + "selectionModel", JMenuBar::getSelectionModel, JMenuBar::setSelectionModel, selectionModel);
    }
  }
}
