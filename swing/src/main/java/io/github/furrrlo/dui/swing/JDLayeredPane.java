package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;
import java.lang.Class;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.function.Supplier;
import javax.swing.JLayeredPane;

@SuppressWarnings("unused")
public class JDLayeredPane {
  public static DeclarativeComponent<JLayeredPane> fn(IdentifiableConsumer<Decorator<JLayeredPane>> body) {
    return fn(JLayeredPane.class, JLayeredPane::new, body);
  }

  public static DeclarativeComponent<JLayeredPane> fn(Supplier<JLayeredPane> factory,
      IdentifiableConsumer<Decorator<JLayeredPane>> body) {
    return fn(JLayeredPane.class, factory, body);
  }

  public static <T extends JLayeredPane> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentifiableConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JLayeredPane> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__JDLayeredPane__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }
  }
}
