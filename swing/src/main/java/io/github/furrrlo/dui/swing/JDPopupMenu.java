package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.*;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.plaf.PopupMenuUI;
import java.awt.*;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDPopupMenu {
  public static DeclarativeComponent<JPopupMenu> fn(IdentityFreeConsumer<Decorator<JPopupMenu>> body) {
    return fn(JPopupMenu.class, JPopupMenu::new, body);
  }

  public static DeclarativeComponent<JPopupMenu> fn(Supplier<JPopupMenu> factory,
      IdentityFreeConsumer<Decorator<JPopupMenu>> body) {
    return fn(JPopupMenu.class, factory, body);
  }

  public static <T extends JPopupMenu> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentityFreeConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JPopupMenu> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__JDPopupMenu__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void ui(IdentityFreeSupplier<? extends PopupMenuUI> ui) {
      attribute(PREFIX + "ui", JPopupMenu::getUI, JPopupMenu::setUI, ui);
    }

    public void borderPainted(IdentityFreeSupplier<Boolean> borderPainted) {
      attribute(PREFIX + "borderPainted", JPopupMenu::isBorderPainted, JPopupMenu::setBorderPainted, borderPainted);
    }

    public void invoker(@Nullable DeclarativeComponentSupplier<? extends Component> invoker) {
      fnAttribute(PREFIX + "invoker", JPopupMenu::getInvoker, JPopupMenu::setInvoker, invoker);
    }

    public void label(IdentityFreeSupplier<String> label) {
      attribute(PREFIX + "label", JPopupMenu::getLabel, JPopupMenu::setLabel, label);
    }

    public void lightWeightPopupEnabled(IdentityFreeSupplier<Boolean> lightWeightPopupEnabled) {
      attribute(PREFIX + "lightWeightPopupEnabled", JPopupMenu::isLightWeightPopupEnabled, JPopupMenu::setLightWeightPopupEnabled, lightWeightPopupEnabled);
    }

    public void popupSize(IdentityFreeSupplier<? extends Dimension> popupSize) {
      attribute(PREFIX + "popupSize", JPopupMenu::setPopupSize, popupSize);
    }

    public void selected(@Nullable DeclarativeComponentSupplier<? extends Component> selected) {
      fnAttribute(PREFIX + "selected", JPopupMenu::setSelected, selected);
    }

    public void selectionModel(IdentityFreeSupplier<? extends SingleSelectionModel> selectionModel) {
      attribute(PREFIX + "selectionModel", JPopupMenu::getSelectionModel, JPopupMenu::setSelectionModel, selectionModel);
    }
  }
}
