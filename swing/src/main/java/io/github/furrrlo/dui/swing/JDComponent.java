package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponentSupplier;
import io.github.furrrlo.dui.IdentityFreeSupplier;
import io.leangen.geantyref.TypeToken;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.AncestorListener;
import java.beans.VetoableChangeListener;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDComponent {

    public static class Decorator<T extends JComponent> extends DAwtContainer.Decorator<T> {

        private static final String PREFIX = "__JDComponent__";

        protected Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);
        }

        protected Decorator(TypeToken<T> type, Supplier<T> factory) {
            super(type, factory);
        }

        public void actionMap(IdentityFreeSupplier<? extends ActionMap> actionMap) {
            attribute(PREFIX + "actionMap", JComponent::getActionMap, JComponent::setActionMap, actionMap);
        }

        public void alignmentX(IdentityFreeSupplier<Float> alignmentX) {
            attribute(PREFIX + "alignmentX", JComponent::getAlignmentX, JComponent::setAlignmentX, alignmentX);
        }

        public void alignmentY(IdentityFreeSupplier<Float> alignmentY) {
            attribute(PREFIX + "alignmentY", JComponent::getAlignmentY, JComponent::setAlignmentY, alignmentY);
        }

        public void autoscrolls(IdentityFreeSupplier<Boolean> autoscrolls) {
            attribute(PREFIX + "autoscrolls", JComponent::getAutoscrolls, JComponent::setAutoscrolls, autoscrolls);
        }

        public void border(IdentityFreeSupplier<? extends Border> border) {
            attribute(PREFIX + "border", JComponent::getBorder, JComponent::setBorder, border);
        }

        public void componentPopupMenu(
                @Nullable DeclarativeComponentSupplier<? extends JPopupMenu> componentPopupMenu) {
            fnAttribute(PREFIX + "componentPopupMenu", JComponent::getComponentPopupMenu, JComponent::setComponentPopupMenu, componentPopupMenu);
        }

        public void debugGraphicsOptions(IdentityFreeSupplier<Integer> debugGraphicsOptions) {
            attribute(PREFIX + "debugGraphicsOptions", JComponent::getDebugGraphicsOptions, JComponent::setDebugGraphicsOptions, debugGraphicsOptions);
        }

        public void doubleBuffered(IdentityFreeSupplier<Boolean> doubleBuffered) {
            attribute(PREFIX + "doubleBuffered", JComponent::isDoubleBuffered, JComponent::setDoubleBuffered, doubleBuffered);
        }

        public void inheritsPopupMenu(IdentityFreeSupplier<Boolean> inheritsPopupMenu) {
            attribute(PREFIX + "inheritsPopupMenu", JComponent::getInheritsPopupMenu, JComponent::setInheritsPopupMenu, inheritsPopupMenu);
        }

        public void inputVerifier(IdentityFreeSupplier<? extends InputVerifier> inputVerifier) {
            attribute(PREFIX + "inputVerifier", JComponent::getInputVerifier, JComponent::setInputVerifier, inputVerifier);
        }

        public void opaque(IdentityFreeSupplier<Boolean> opaque) {
            attribute(PREFIX + "opaque", JComponent::isOpaque, JComponent::setOpaque, opaque);
        }

        public void requestFocusEnabled(IdentityFreeSupplier<Boolean> requestFocusEnabled) {
            attribute(PREFIX + "requestFocusEnabled", JComponent::isRequestFocusEnabled, JComponent::setRequestFocusEnabled, requestFocusEnabled);
        }

        public void toolTipText(IdentityFreeSupplier<String> toolTipText) {
            attribute(PREFIX + "toolTipText", JComponent::getToolTipText, JComponent::setToolTipText, toolTipText);
        }

        public void transferHandler(IdentityFreeSupplier<? extends TransferHandler> transferHandler) {
            attribute(PREFIX + "transferHandler", JComponent::getTransferHandler, JComponent::setTransferHandler, transferHandler);
        }

        public void verifyInputWhenFocusTarget(IdentityFreeSupplier<Boolean> verifyInputWhenFocusTarget) {
            attribute(PREFIX + "verifyInputWhenFocusTarget", JComponent::getVerifyInputWhenFocusTarget, JComponent::setVerifyInputWhenFocusTarget, verifyInputWhenFocusTarget);
        }

        public void ancestorListener(AncestorListener ancestorListener) {
            eventListener(
                    PREFIX + "ancestorListener",
                    AncestorListener.class,
                    AncestorListenerWrapper::new,
                    JComponent::addAncestorListener,
                    ancestorListener);
        }

        public void vetoableChangeListener(VetoableChangeListener vetoableChangeListener) {
            eventListener(
                    PREFIX + "vetoableChangeListener",
                    VetoableChangeListener.class,
                    VetoableChangeListenerWrapper::new,
                    JComponent::addVetoableChangeListener,
                    vetoableChangeListener);
        }
    }
}
