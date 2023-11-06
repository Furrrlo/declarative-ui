package io.github.furrrlo.dui.swing.plaf.basic;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;
import io.github.furrrlo.dui.swing.JDPopupMenu;
import java.lang.Class;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.function.Supplier;
import javax.swing.plaf.basic.BasicComboPopup;

@SuppressWarnings("unused")
public class DBasicComboPopup {
  public DeclarativeComponent<BasicComboPopup> fn(
      IdentifiableConsumer<Decorator<BasicComboPopup>> body) {
    return fn(BasicComboPopup.class, BasicComboPopup::new, body);
  }

  public DeclarativeComponent<BasicComboPopup> fn(Supplier<BasicComboPopup> factory,
      IdentifiableConsumer<Decorator<BasicComboPopup>> body) {
    return fn(BasicComboPopup.class, factory, body);
  }

  public <T extends BasicComboPopup> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentifiableConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends BasicComboPopup> extends JDPopupMenu.Decorator<T> {
    private static final String PREFIX = "__DBasicComboPopup__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }
  }
}
