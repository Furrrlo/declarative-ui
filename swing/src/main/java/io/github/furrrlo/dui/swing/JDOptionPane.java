package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.IdentifiableConsumer;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.Integer;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.function.Supplier;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.plaf.OptionPaneUI;

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

    public void ui(Supplier<? extends OptionPaneUI> ui) {
      attribute(PREFIX + "ui", JOptionPane::getUI, JOptionPane::setUI, ui);
    }

    public void icon(Supplier<? extends Icon> icon) {
      attribute(PREFIX + "icon", JOptionPane::getIcon, JOptionPane::setIcon, icon);
    }

    public void initialSelectionValue(Supplier<?> initialSelectionValue) {
      attribute(PREFIX + "initialSelectionValue", JOptionPane::getInitialSelectionValue, JOptionPane::setInitialSelectionValue, initialSelectionValue);
    }

    public void initialValue(Supplier<?> initialValue) {
      attribute(PREFIX + "initialValue", JOptionPane::getInitialValue, JOptionPane::setInitialValue, initialValue);
    }

    public void inputValue(Supplier<?> inputValue) {
      attribute(PREFIX + "inputValue", JOptionPane::getInputValue, JOptionPane::setInputValue, inputValue);
    }

    public void message(Supplier<?> message) {
      attribute(PREFIX + "message", JOptionPane::getMessage, JOptionPane::setMessage, message);
    }

    public void messageType(Supplier<Integer> messageType) {
      attribute(PREFIX + "messageType", JOptionPane::getMessageType, JOptionPane::setMessageType, messageType);
    }

    public void optionType(Supplier<Integer> optionType) {
      attribute(PREFIX + "optionType", JOptionPane::getOptionType, JOptionPane::setOptionType, optionType);
    }

    public void options() {
      // TODO: implement "options"
    }

    public void selectionValues() {
      // TODO: implement "selectionValues"
    }

    public void value(Supplier<?> value) {
      attribute(PREFIX + "value", JOptionPane::getValue, JOptionPane::setValue, value);
    }

    public void wantsInput(Supplier<Boolean> wantsInput) {
      attribute(PREFIX + "wantsInput", JOptionPane::getWantsInput, JOptionPane::setWantsInput, wantsInput);
    }
  }
}
