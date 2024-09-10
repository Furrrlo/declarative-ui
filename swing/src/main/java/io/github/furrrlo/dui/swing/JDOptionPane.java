package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentityFreeConsumer;
import io.github.furrrlo.dui.IdentityFreeSupplier;

import javax.swing.*;
import javax.swing.plaf.OptionPaneUI;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDOptionPane {
  public static DeclarativeComponent<JOptionPane> fn(IdentityFreeConsumer<Decorator<JOptionPane>> body) {
    return fn(JOptionPane.class, JOptionPane::new, body);
  }

  public static DeclarativeComponent<JOptionPane> fn(Supplier<JOptionPane> factory,
      IdentityFreeConsumer<Decorator<JOptionPane>> body) {
    return fn(JOptionPane.class, factory, body);
  }

  public static <T extends JOptionPane> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentityFreeConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JOptionPane> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__JDOptionPane__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void ui(IdentityFreeSupplier<? extends OptionPaneUI> ui) {
      attribute(PREFIX + "ui", JOptionPane::getUI, JOptionPane::setUI, ui);
    }

    public void icon(IdentityFreeSupplier<? extends Icon> icon) {
      attribute(PREFIX + "icon", JOptionPane::getIcon, JOptionPane::setIcon, icon);
    }

    public void initialSelectionValue(IdentityFreeSupplier<?> initialSelectionValue) {
      attribute(PREFIX + "initialSelectionValue", JOptionPane::getInitialSelectionValue, JOptionPane::setInitialSelectionValue, initialSelectionValue);
    }

    public void initialValue(IdentityFreeSupplier<?> initialValue) {
      attribute(PREFIX + "initialValue", JOptionPane::getInitialValue, JOptionPane::setInitialValue, initialValue);
    }

    public void inputValue(IdentityFreeSupplier<?> inputValue) {
      attribute(PREFIX + "inputValue", JOptionPane::getInputValue, JOptionPane::setInputValue, inputValue);
    }

    public void message(IdentityFreeSupplier<?> message) {
      attribute(PREFIX + "message", JOptionPane::getMessage, JOptionPane::setMessage, message);
    }

    public void messageType(IdentityFreeSupplier<Integer> messageType) {
      attribute(PREFIX + "messageType", JOptionPane::getMessageType, JOptionPane::setMessageType, messageType);
    }

    public void optionType(IdentityFreeSupplier<Integer> optionType) {
      attribute(PREFIX + "optionType", JOptionPane::getOptionType, JOptionPane::setOptionType, optionType);
    }

    public void options() {
      // TODO: implement "options"
    }

    public void selectionValues() {
      // TODO: implement "selectionValues"
    }

    public void value(IdentityFreeSupplier<?> value) {
      attribute(PREFIX + "value", JOptionPane::getValue, JOptionPane::setValue, value);
    }

    public void wantsInput(IdentityFreeSupplier<Boolean> wantsInput) {
      attribute(PREFIX + "wantsInput", JOptionPane::getWantsInput, JOptionPane::setWantsInput, wantsInput);
    }
  }
}
