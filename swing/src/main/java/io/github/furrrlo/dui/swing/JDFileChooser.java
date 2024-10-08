package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.*;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
import java.io.File;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDFileChooser {
  public static DeclarativeComponent<JFileChooser> fn(IdentityFreeConsumer<Decorator<JFileChooser>> body) {
    return fn(JFileChooser.class, JFileChooser::new, body);
  }

  public static DeclarativeComponent<JFileChooser> fn(Supplier<JFileChooser> factory,
      IdentityFreeConsumer<Decorator<JFileChooser>> body) {
    return fn(JFileChooser.class, factory, body);
  }

  public static <T extends JFileChooser> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentityFreeConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JFileChooser> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__JDFileChooser__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void acceptAllFileFilterUsed(IdentityFreeSupplier<Boolean> acceptAllFileFilterUsed) {
      attribute(PREFIX + "acceptAllFileFilterUsed", JFileChooser::isAcceptAllFileFilterUsed, JFileChooser::setAcceptAllFileFilterUsed, acceptAllFileFilterUsed);
    }

    public void accessory(@Nullable DeclarativeComponentSupplier<? extends JComponent> accessory) {
      fnAttribute(PREFIX + "accessory", JFileChooser::getAccessory, JFileChooser::setAccessory, accessory);
    }

    public void approveButtonMnemonic(IdentityFreeSupplier<Integer> approveButtonMnemonic) {
      attribute(PREFIX + "approveButtonMnemonic", JFileChooser::getApproveButtonMnemonic, JFileChooser::setApproveButtonMnemonic, approveButtonMnemonic);
    }

    public void approveButtonText(IdentityFreeSupplier<String> approveButtonText) {
      attribute(PREFIX + "approveButtonText", JFileChooser::getApproveButtonText, JFileChooser::setApproveButtonText, approveButtonText);
    }

    public void approveButtonToolTipText(IdentityFreeSupplier<String> approveButtonToolTipText) {
      attribute(PREFIX + "approveButtonToolTipText", JFileChooser::getApproveButtonToolTipText, JFileChooser::setApproveButtonToolTipText, approveButtonToolTipText);
    }

    public void controlButtonsAreShown(IdentityFreeSupplier<Boolean> controlButtonsAreShown) {
      attribute(PREFIX + "controlButtonsAreShown", JFileChooser::getControlButtonsAreShown, JFileChooser::setControlButtonsAreShown, controlButtonsAreShown);
    }

    public void currentDirectory(IdentityFreeSupplier<? extends File> currentDirectory) {
      attribute(PREFIX + "currentDirectory", JFileChooser::getCurrentDirectory, JFileChooser::setCurrentDirectory, currentDirectory);
    }

    public void dialogTitle(IdentityFreeSupplier<String> dialogTitle) {
      attribute(PREFIX + "dialogTitle", JFileChooser::getDialogTitle, JFileChooser::setDialogTitle, dialogTitle);
    }

    public void dialogType(IdentityFreeSupplier<Integer> dialogType) {
      attribute(PREFIX + "dialogType", JFileChooser::getDialogType, JFileChooser::setDialogType, dialogType);
    }

    public void dragEnabled(IdentityFreeSupplier<Boolean> dragEnabled) {
      attribute(PREFIX + "dragEnabled", JFileChooser::getDragEnabled, JFileChooser::setDragEnabled, dragEnabled);
    }

    public void fileFilter(IdentityFreeSupplier<? extends FileFilter> fileFilter) {
      attribute(PREFIX + "fileFilter", JFileChooser::getFileFilter, JFileChooser::setFileFilter, fileFilter);
    }

    public void fileHidingEnabled(IdentityFreeSupplier<Boolean> fileHidingEnabled) {
      attribute(PREFIX + "fileHidingEnabled", JFileChooser::isFileHidingEnabled, JFileChooser::setFileHidingEnabled, fileHidingEnabled);
    }

    public void fileSelectionMode(IdentityFreeSupplier<Integer> fileSelectionMode) {
      attribute(PREFIX + "fileSelectionMode", JFileChooser::getFileSelectionMode, JFileChooser::setFileSelectionMode, fileSelectionMode);
    }

    public void fileSystemView(IdentityFreeSupplier<? extends FileSystemView> fileSystemView) {
      attribute(PREFIX + "fileSystemView", JFileChooser::getFileSystemView, JFileChooser::setFileSystemView, fileSystemView);
    }

    public void fileView(IdentityFreeSupplier<? extends FileView> fileView) {
      attribute(PREFIX + "fileView", JFileChooser::getFileView, JFileChooser::setFileView, fileView);
    }

    public void multiSelectionEnabled(IdentityFreeSupplier<Boolean> multiSelectionEnabled) {
      attribute(PREFIX + "multiSelectionEnabled", JFileChooser::isMultiSelectionEnabled, JFileChooser::setMultiSelectionEnabled, multiSelectionEnabled);
    }

    public void selectedFile(IdentityFreeSupplier<? extends File> selectedFile) {
      attribute(PREFIX + "selectedFile", JFileChooser::getSelectedFile, JFileChooser::setSelectedFile, selectedFile);
    }

    public void selectedFiles() {
      // TODO: implement "selectedFiles"
    }
  }
}
