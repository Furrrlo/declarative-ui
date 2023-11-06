package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.DeclarativeComponentSupplier;
import io.github.furrrlo.dui.IdentifiableConsumer;
import java.lang.Class;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.function.Supplier;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.plaf.SpinnerUI;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class JDSpinner {
  public static DeclarativeComponent<JSpinner> fn(IdentifiableConsumer<Decorator<JSpinner>> body) {
    return fn(JSpinner.class, JSpinner::new, body);
  }

  public static DeclarativeComponent<JSpinner> fn(Supplier<JSpinner> factory,
      IdentifiableConsumer<Decorator<JSpinner>> body) {
    return fn(JSpinner.class, factory, body);
  }

  public static <T extends JSpinner> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentifiableConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JSpinner> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__JDSpinner__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void ui(Supplier<? extends SpinnerUI> ui) {
      attribute(PREFIX + "ui", JSpinner::getUI, JSpinner::setUI, ui);
    }

    public void editor(@Nullable DeclarativeComponentSupplier<? extends JComponent> editor) {
      fnAttribute(PREFIX + "editor", JSpinner::getEditor, JSpinner::setEditor, editor);
    }

    public void model(Supplier<? extends SpinnerModel> model) {
      attribute(PREFIX + "model", JSpinner::getModel, JSpinner::setModel, model);
    }

    public void value(Supplier<?> value) {
      attribute(PREFIX + "value", JSpinner::getValue, JSpinner::setValue, value);
    }
  }
}
