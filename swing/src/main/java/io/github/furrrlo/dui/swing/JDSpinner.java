package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.*;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.plaf.SpinnerUI;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDSpinner {
  public static DeclarativeComponent<JSpinner> fn(IdentityFreeConsumer<Decorator<JSpinner>> body) {
    return fn(JSpinner.class, JSpinner::new, body);
  }

  public static DeclarativeComponent<JSpinner> fn(Supplier<JSpinner> factory,
      IdentityFreeConsumer<Decorator<JSpinner>> body) {
    return fn(JSpinner.class, factory, body);
  }

  public static <T extends JSpinner> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentityFreeConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JSpinner> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__JDSpinner__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void ui(IdentityFreeSupplier<? extends SpinnerUI> ui) {
      attribute(PREFIX + "ui", JSpinner::getUI, JSpinner::setUI, ui);
    }

    public void editor(@Nullable DeclarativeComponentSupplier<? extends JComponent> editor) {
      fnAttribute(PREFIX + "editor", JSpinner::getEditor, JSpinner::setEditor, editor);
    }

    public void model(IdentityFreeSupplier<? extends SpinnerModel> model) {
      attribute(PREFIX + "model", JSpinner::getModel, JSpinner::setModel, model);
    }

    public void value(IdentityFreeSupplier<?> value) {
      attribute(PREFIX + "value", JSpinner::getValue, JSpinner::setValue, value);
    }
  }
}
