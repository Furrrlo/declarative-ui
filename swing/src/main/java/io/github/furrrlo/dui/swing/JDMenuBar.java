package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.DeclarativeComponentSupplier;
import io.github.furrrlo.dui.IdentifiableConsumer;
import java.awt.Insets;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.function.Supplier;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.SingleSelectionModel;
import javax.swing.plaf.MenuBarUI;
import org.jetbrains.annotations.Nullable;

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

    public void ui(Supplier<? extends MenuBarUI> ui) {
      attribute(PREFIX + "ui", JMenuBar::getUI, JMenuBar::setUI, ui);
    }

    public void borderPainted(Supplier<Boolean> borderPainted) {
      attribute(PREFIX + "borderPainted", JMenuBar::isBorderPainted, JMenuBar::setBorderPainted, borderPainted);
    }

    public void helpMenu(@Nullable DeclarativeComponentSupplier<? extends JMenu> helpMenu) {
      fnAttribute(PREFIX + "helpMenu", JMenuBar::getHelpMenu, JMenuBar::setHelpMenu, helpMenu);
    }

    public void margin(Supplier<? extends Insets> margin) {
      attribute(PREFIX + "margin", JMenuBar::getMargin, JMenuBar::setMargin, margin);
    }

    public void selectionModel(Supplier<? extends SingleSelectionModel> selectionModel) {
      attribute(PREFIX + "selectionModel", JMenuBar::getSelectionModel, JMenuBar::setSelectionModel, selectionModel);
    }
  }
}
