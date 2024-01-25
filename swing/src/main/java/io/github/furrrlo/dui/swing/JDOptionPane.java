package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;
import io.github.furrrlo.dui.IdentifiableSupplier;

import javax.swing.*;
import javax.swing.plaf.OptionPaneUI;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDOptionPane {
  public static DeclarativeComponent<JOptionPane> fn(IdentifiableConsumer<Decorator<JOptionPane>> body) {
    return fn(JOptionPane.class, JOptionPane::new, body);
  }

  public static DeclarativeComponent<JOptionPane> fn(Supplier<JOptionPane> factory,
      IdentifiableConsumer<Decorator<JOptionPane>> body) {
    return fn(JOptionPane.class, factory, body);
  }

  public static <T extends JOptionPane> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentifiableConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JOptionPane> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__JDOptionPane__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void ui(IdentifiableSupplier<? extends OptionPaneUI> ui) {
      attribute(PREFIX + "ui", JOptionPane::getUI, JOptionPane::setUI, ui);
    }

    public void icon(IdentifiableSupplier<? extends Icon> icon) {
      attribute(PREFIX + "icon", JOptionPane::getIcon, JOptionPane::setIcon, icon);
    }

    public void initialSelectionValue(IdentifiableSupplier<?> initialSelectionValue) {
      attribute(PREFIX + "initialSelectionValue", JOptionPane::getInitialSelectionValue, JOptionPane::setInitialSelectionValue, initialSelectionValue);
    }

    public void initialValue(IdentifiableSupplier<?> initialValue) {
      attribute(PREFIX + "initialValue", JOptionPane::getInitialValue, JOptionPane::setInitialValue, initialValue);
    }

    public void inputValue(IdentifiableSupplier<?> inputValue) {
      attribute(PREFIX + "inputValue", JOptionPane::getInputValue, JOptionPane::setInputValue, inputValue);
    }

    public void message(IdentifiableSupplier<?> message) {
      attribute(PREFIX + "message", JOptionPane::getMessage, JOptionPane::setMessage, message);
    }

    public void messageType(IdentifiableSupplier<Integer> messageType) {
      attribute(PREFIX + "messageType", JOptionPane::getMessageType, JOptionPane::setMessageType, messageType);
    }

    public void optionType(IdentifiableSupplier<Integer> optionType) {
      attribute(PREFIX + "optionType", JOptionPane::getOptionType, JOptionPane::setOptionType, optionType);
    }

    public void options() {
      // TODO: implement "options"
    }

    public void selectionValues() {
      // TODO: implement "selectionValues"
    }

    public void value(IdentifiableSupplier<?> value) {
      attribute(PREFIX + "value", JOptionPane::getValue, JOptionPane::setValue, value);
    }

    public void wantsInput(IdentifiableSupplier<Boolean> wantsInput) {
      attribute(PREFIX + "wantsInput", JOptionPane::getWantsInput, JOptionPane::setWantsInput, wantsInput);
    }
  }
}
