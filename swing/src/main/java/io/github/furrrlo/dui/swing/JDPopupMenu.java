package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.DeclarativeComponentSupplier;
import io.github.furrrlo.dui.IdentifiableConsumer;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.plaf.PopupMenuUI;
import java.awt.*;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDPopupMenu {
  public static DeclarativeComponent<JPopupMenu> fn(IdentifiableConsumer<Decorator<JPopupMenu>> body) {
    return fn(JPopupMenu.class, JPopupMenu::new, body);
  }

  public static DeclarativeComponent<JPopupMenu> fn(Supplier<JPopupMenu> factory,
      IdentifiableConsumer<Decorator<JPopupMenu>> body) {
    return fn(JPopupMenu.class, factory, body);
  }

  public static <T extends JPopupMenu> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentifiableConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JPopupMenu> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__JDPopupMenu__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void ui(Supplier<? extends PopupMenuUI> ui) {
      attribute(PREFIX + "ui", JPopupMenu::getUI, JPopupMenu::setUI, ui);
    }

    public void borderPainted(Supplier<Boolean> borderPainted) {
      attribute(PREFIX + "borderPainted", JPopupMenu::isBorderPainted, JPopupMenu::setBorderPainted, borderPainted);
    }

    public void invoker(@Nullable DeclarativeComponentSupplier<? extends Component> invoker) {
      fnAttribute(PREFIX + "invoker", JPopupMenu::getInvoker, JPopupMenu::setInvoker, invoker);
    }

    public void label(Supplier<String> label) {
      attribute(PREFIX + "label", JPopupMenu::getLabel, JPopupMenu::setLabel, label);
    }

    public void lightWeightPopupEnabled(Supplier<Boolean> lightWeightPopupEnabled) {
      attribute(PREFIX + "lightWeightPopupEnabled", JPopupMenu::isLightWeightPopupEnabled, JPopupMenu::setLightWeightPopupEnabled, lightWeightPopupEnabled);
    }

    public void popupSize(Supplier<? extends Dimension> popupSize) {
      attribute(PREFIX + "popupSize", JPopupMenu::setPopupSize, popupSize);
    }

    public void selected(@Nullable DeclarativeComponentSupplier<? extends Component> selected) {
      fnAttribute(PREFIX + "selected", JPopupMenu::setSelected, selected);
    }

    public void selectionModel(Supplier<? extends SingleSelectionModel> selectionModel) {
      attribute(PREFIX + "selectionModel", JPopupMenu::getSelectionModel, JPopupMenu::setSelectionModel, selectionModel);
    }
  }
}
