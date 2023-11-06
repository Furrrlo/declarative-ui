package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.DeclarativeComponentSupplier;
import io.github.furrrlo.dui.IdentifiableConsumer;
import java.awt.Component;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.Double;
import java.lang.Integer;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.function.Supplier;
import javax.swing.JSplitPane;
import javax.swing.plaf.SplitPaneUI;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class JDSplitPane {
  public static DeclarativeComponent<JSplitPane> fn(IdentifiableConsumer<Decorator<JSplitPane>> body) {
    return fn(JSplitPane.class, JSplitPane::new, body);
  }

  public static DeclarativeComponent<JSplitPane> fn(Supplier<JSplitPane> factory,
      IdentifiableConsumer<Decorator<JSplitPane>> body) {
    return fn(JSplitPane.class, factory, body);
  }

  public static <T extends JSplitPane> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentifiableConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JSplitPane> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__JDSplitPane__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void ui(Supplier<? extends SplitPaneUI> ui) {
      attribute(PREFIX + "ui", JSplitPane::getUI, JSplitPane::setUI, ui);
    }

    public void bottomComponent(
        @Nullable DeclarativeComponentSupplier<? extends Component> bottomComponent) {
      fnAttribute(PREFIX + "bottomComponent", JSplitPane::getBottomComponent, JSplitPane::setBottomComponent, bottomComponent);
    }

    public void continuousLayout(Supplier<Boolean> continuousLayout) {
      attribute(PREFIX + "continuousLayout", JSplitPane::isContinuousLayout, JSplitPane::setContinuousLayout, continuousLayout);
    }

    public void dividerLocation(Supplier<Integer> dividerLocation) {
      attribute(PREFIX + "dividerLocation", JSplitPane::getDividerLocation, JSplitPane::setDividerLocation, dividerLocation);
    }

    public void dividerSize(Supplier<Integer> dividerSize) {
      attribute(PREFIX + "dividerSize", JSplitPane::getDividerSize, JSplitPane::setDividerSize, dividerSize);
    }

    public void lastDividerLocation(Supplier<Integer> lastDividerLocation) {
      attribute(PREFIX + "lastDividerLocation", JSplitPane::getLastDividerLocation, JSplitPane::setLastDividerLocation, lastDividerLocation);
    }

    public void leftComponent(
        @Nullable DeclarativeComponentSupplier<? extends Component> leftComponent) {
      fnAttribute(PREFIX + "leftComponent", JSplitPane::getLeftComponent, JSplitPane::setLeftComponent, leftComponent);
    }

    public void oneTouchExpandable(Supplier<Boolean> oneTouchExpandable) {
      attribute(PREFIX + "oneTouchExpandable", JSplitPane::isOneTouchExpandable, JSplitPane::setOneTouchExpandable, oneTouchExpandable);
    }

    public void orientation(Supplier<Integer> orientation) {
      attribute(PREFIX + "orientation", JSplitPane::getOrientation, JSplitPane::setOrientation, orientation);
    }

    public void resizeWeight(Supplier<Double> resizeWeight) {
      attribute(PREFIX + "resizeWeight", JSplitPane::getResizeWeight, JSplitPane::setResizeWeight, resizeWeight);
    }

    public void rightComponent(
        @Nullable DeclarativeComponentSupplier<? extends Component> rightComponent) {
      fnAttribute(PREFIX + "rightComponent", JSplitPane::getRightComponent, JSplitPane::setRightComponent, rightComponent);
    }

    public void topComponent(
        @Nullable DeclarativeComponentSupplier<? extends Component> topComponent) {
      fnAttribute(PREFIX + "topComponent", JSplitPane::getTopComponent, JSplitPane::setTopComponent, topComponent);
    }
  }
}
