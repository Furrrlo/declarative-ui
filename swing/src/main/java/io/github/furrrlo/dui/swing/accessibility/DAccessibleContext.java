package io.github.furrrlo.dui.swing.accessibility;

import io.github.furrrlo.dui.IdentityFreeSupplier;
import io.github.furrrlo.dui.swing.SwingDecorator;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import java.util.function.Supplier;

public class DAccessibleContext {
  public static class Decorator<T extends AccessibleContext> extends SwingDecorator<T> {
    private static final String PREFIX = "__DAccessibleContext__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void accessibleDescription(IdentityFreeSupplier<String> accessibleDescription) {
      attribute(PREFIX + "accessibleDescription", AccessibleContext::getAccessibleDescription, AccessibleContext::setAccessibleDescription, accessibleDescription);
    }

    public void accessibleName(IdentityFreeSupplier<String> accessibleName) {
      attribute(PREFIX + "accessibleName", AccessibleContext::getAccessibleName, AccessibleContext::setAccessibleName, accessibleName);
    }

    public void accessibleParent(IdentityFreeSupplier<? extends Accessible> accessibleParent) {
      attribute(PREFIX + "accessibleParent", AccessibleContext::getAccessibleParent, AccessibleContext::setAccessibleParent, accessibleParent);
    }
  }
}