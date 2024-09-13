package io.github.furrrlo.dui.cmptw;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CompletableFuture;

class SelectDeviceDialog extends JDialog {

    private final CompletableFuture<KeyboardHookDevice> deviceFuture;

    public SelectDeviceDialog(CompletableFuture<KeyboardHookDevice> deviceFuture) {
        super(null, ModalityType.APPLICATION_MODAL);
        this.deviceFuture = deviceFuture;
        init();
    }

    private SelectDeviceDialog(Frame owner, CompletableFuture<KeyboardHookDevice> deviceFuture) {
        super(owner, ModalityType.APPLICATION_MODAL);
        this.deviceFuture = deviceFuture;
        init();
    }

    public SelectDeviceDialog(Dialog owner, CompletableFuture<KeyboardHookDevice> deviceFuture) {
        super(owner, ModalityType.APPLICATION_MODAL);
        this.deviceFuture = deviceFuture;
        init();
    }

    public SelectDeviceDialog(Window owner, CompletableFuture<KeyboardHookDevice> deviceFuture) {
        super(owner, ModalityType.APPLICATION_MODAL);
        this.deviceFuture = deviceFuture;
        init();
    }

    private void init() {
        setTitle("Select Device");

        final JPanel contentPane = new JPanel();
        contentPane.setLayout(new MigLayout(new LC().fill().align("center", "center")));
        contentPane.add(new JLabel("Press any key on the target device"), new CC().alignX("center").alignY("center"));
        add(contentPane);

        pack();
        setMinimumSize(new Dimension(Math.max(200, getWidth()), Math.max(100, getHeight())));
        setLocationRelativeTo(getOwner());

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

//        keyboardHookService.addListener((service, listener, event) -> {
//            if(!isFocused())
//                return KeyboardHookListener.ListenerResult.CONTINUE;
//
//            deviceFuture.complete(event.device());
//            service.removeListener(listener);
//            setVisible(false);
//            return KeyboardHookListener.ListenerResult.DELETE;
//        });
    }

    @Override
    public void dispose() {
        super.dispose();
        deviceFuture.complete(null);
    }

    public static CompletableFuture<KeyboardHookDevice> selectDevice() {
        final var future = new CompletableFuture<KeyboardHookDevice>();
        new SelectDeviceDialog(future).setVisible(true);
        return future;
    }

    public static CompletableFuture<KeyboardHookDevice> selectDevice(Frame owner) {
        final var future = new CompletableFuture<KeyboardHookDevice>();
        new SelectDeviceDialog(owner, future).setVisible(true);
        return future;
    }

    public static CompletableFuture<KeyboardHookDevice> selectDevice(Dialog owner) {
        final var future = new CompletableFuture<KeyboardHookDevice>();
        new SelectDeviceDialog(owner, future).setVisible(true);
        return future;
    }

    public static CompletableFuture<KeyboardHookDevice> selectDevice(Window owner) {
        final var future = new CompletableFuture<KeyboardHookDevice>();
        new SelectDeviceDialog(owner, future).setVisible(true);
        return future;
    }
}
