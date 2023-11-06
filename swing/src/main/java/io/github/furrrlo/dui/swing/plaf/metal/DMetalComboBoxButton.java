package io.github.furrrlo.dui.swing.plaf.metal;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.DeclarativeComponentSupplier;
import io.github.furrrlo.dui.IdentifiableConsumer;
import io.github.furrrlo.dui.swing.JDButton;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.function.Supplier;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.plaf.metal.MetalComboBoxButton;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class DMetalComboBoxButton {
  public DeclarativeComponent<MetalComboBoxButton> fn(
      IdentifiableConsumer<Decorator<MetalComboBoxButton>> body) {
    return fn(MetalComboBoxButton.class, MetalComboBoxButton::new, body);
  }

  public DeclarativeComponent<MetalComboBoxButton> fn(Supplier<MetalComboBoxButton> factory,
      IdentifiableConsumer<Decorator<MetalComboBoxButton>> body) {
    return fn(MetalComboBoxButton.class, factory, body);
  }

  public <T extends MetalComboBoxButton> DeclarativeComponent<T> fn(Class<T> type,
      Supplier<T> factory, IdentifiableConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends MetalComboBoxButton> extends JDButton.Decorator<T> {
    private static final String PREFIX = "__DMetalComboBoxButton__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void comboBox(@Nullable DeclarativeComponentSupplier<? extends JComboBox> comboBox) {
      fnAttribute(PREFIX + "comboBox", MetalComboBoxButton::getComboBox, MetalComboBoxButton::setComboBox, comboBox);
    }

    public void comboIcon(Supplier<? extends Icon> comboIcon) {
      attribute(PREFIX + "comboIcon", MetalComboBoxButton::getComboIcon, MetalComboBoxButton::setComboIcon, comboIcon);
    }

    public void enabled(Supplier<Boolean> enabled) {
      attribute(PREFIX + "enabled", MetalComboBoxButton::setEnabled, enabled);
    }

    public void iconOnly(Supplier<Boolean> iconOnly) {
      attribute(PREFIX + "iconOnly", MetalComboBoxButton::isIconOnly, MetalComboBoxButton::setIconOnly, iconOnly);
    }
  }
}
