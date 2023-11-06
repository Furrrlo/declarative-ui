package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentFactory;
import io.github.furrrlo.dui.DeclarativeComponentSupplier;
import io.github.furrrlo.dui.IdentifiableConsumer;
import java.io.File;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.Integer;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.function.Supplier;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class JDFileChooser {
  public static DeclarativeComponent<JFileChooser> fn(IdentifiableConsumer<Decorator<JFileChooser>> body) {
    return fn(JFileChooser.class, JFileChooser::new, body);
  }

  public static DeclarativeComponent<JFileChooser> fn(Supplier<JFileChooser> factory,
      IdentifiableConsumer<Decorator<JFileChooser>> body) {
    return fn(JFileChooser.class, factory, body);
  }

  public static <T extends JFileChooser> DeclarativeComponent<T> fn(Class<T> type, Supplier<T> factory,
      IdentifiableConsumer<Decorator<T>> body) {
    return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
  }

  public static class Decorator<T extends JFileChooser> extends JDComponent.Decorator<T> {
    private static final String PREFIX = "__JDFileChooser__";

    protected Decorator(Class<T> type, Supplier<T> factory) {
      super(type, factory);
    }

    public void acceptAllFileFilterUsed(Supplier<Boolean> acceptAllFileFilterUsed) {
      attribute(PREFIX + "acceptAllFileFilterUsed", JFileChooser::isAcceptAllFileFilterUsed, JFileChooser::setAcceptAllFileFilterUsed, acceptAllFileFilterUsed);
    }

    public void accessory(@Nullable DeclarativeComponentSupplier<? extends JComponent> accessory) {
      fnAttribute(PREFIX + "accessory", JFileChooser::getAccessory, JFileChooser::setAccessory, accessory);
    }

    public void approveButtonMnemonic(Supplier<Integer> approveButtonMnemonic) {
      attribute(PREFIX + "approveButtonMnemonic", JFileChooser::getApproveButtonMnemonic, JFileChooser::setApproveButtonMnemonic, approveButtonMnemonic);
    }

    public void approveButtonText(Supplier<String> approveButtonText) {
      attribute(PREFIX + "approveButtonText", JFileChooser::getApproveButtonText, JFileChooser::setApproveButtonText, approveButtonText);
    }

    public void approveButtonToolTipText(Supplier<String> approveButtonToolTipText) {
      attribute(PREFIX + "approveButtonToolTipText", JFileChooser::getApproveButtonToolTipText, JFileChooser::setApproveButtonToolTipText, approveButtonToolTipText);
    }

    public void controlButtonsAreShown(Supplier<Boolean> controlButtonsAreShown) {
      attribute(PREFIX + "controlButtonsAreShown", JFileChooser::getControlButtonsAreShown, JFileChooser::setControlButtonsAreShown, controlButtonsAreShown);
    }

    public void currentDirectory(Supplier<? extends File> currentDirectory) {
      attribute(PREFIX + "currentDirectory", JFileChooser::getCurrentDirectory, JFileChooser::setCurrentDirectory, currentDirectory);
    }

    public void dialogTitle(Supplier<String> dialogTitle) {
      attribute(PREFIX + "dialogTitle", JFileChooser::getDialogTitle, JFileChooser::setDialogTitle, dialogTitle);
    }

    public void dialogType(Supplier<Integer> dialogType) {
      attribute(PREFIX + "dialogType", JFileChooser::getDialogType, JFileChooser::setDialogType, dialogType);
    }

    public void dragEnabled(Supplier<Boolean> dragEnabled) {
      attribute(PREFIX + "dragEnabled", JFileChooser::getDragEnabled, JFileChooser::setDragEnabled, dragEnabled);
    }

    public void fileFilter(Supplier<? extends FileFilter> fileFilter) {
      attribute(PREFIX + "fileFilter", JFileChooser::getFileFilter, JFileChooser::setFileFilter, fileFilter);
    }

    public void fileHidingEnabled(Supplier<Boolean> fileHidingEnabled) {
      attribute(PREFIX + "fileHidingEnabled", JFileChooser::isFileHidingEnabled, JFileChooser::setFileHidingEnabled, fileHidingEnabled);
    }

    public void fileSelectionMode(Supplier<Integer> fileSelectionMode) {
      attribute(PREFIX + "fileSelectionMode", JFileChooser::getFileSelectionMode, JFileChooser::setFileSelectionMode, fileSelectionMode);
    }

    public void fileSystemView(Supplier<? extends FileSystemView> fileSystemView) {
      attribute(PREFIX + "fileSystemView", JFileChooser::getFileSystemView, JFileChooser::setFileSystemView, fileSystemView);
    }

    public void fileView(Supplier<? extends FileView> fileView) {
      attribute(PREFIX + "fileView", JFileChooser::getFileView, JFileChooser::setFileView, fileView);
    }

    public void multiSelectionEnabled(Supplier<Boolean> multiSelectionEnabled) {
      attribute(PREFIX + "multiSelectionEnabled", JFileChooser::isMultiSelectionEnabled, JFileChooser::setMultiSelectionEnabled, multiSelectionEnabled);
    }

    public void selectedFile(Supplier<? extends File> selectedFile) {
      attribute(PREFIX + "selectedFile", JFileChooser::getSelectedFile, JFileChooser::setSelectedFile, selectedFile);
    }

    public void selectedFiles() {
      // TODO: implement "selectedFiles"
    }
  }
}
