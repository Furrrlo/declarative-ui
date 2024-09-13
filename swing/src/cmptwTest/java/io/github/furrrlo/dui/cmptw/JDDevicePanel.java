package io.github.furrrlo.dui.cmptw;

import io.github.furrrlo.dui.DWrapper;
import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.IdentityFreeSupplier;
import io.github.furrrlo.dui.Memo;
import io.github.furrrlo.dui.swing.*;
import jiconfont.icons.font_awesome.FontAwesome;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static io.github.furrrlo.dui.Hooks.useMemo;
import static io.github.furrrlo.dui.Hooks.useState;

class JDDevicePanel extends JPanel {

    protected static final int TAB_ICON_SIZE = 32;

    public static DeclarativeComponent<? extends Container> fn(Supplier<Boolean> visible,
                                                               Supplier<Hook> hook,
                                                               Consumer<Hook> setHook,
                                                               Supplier<Hook.Device> device,
                                                               Consumer<Hook.Device> setDevice) {
        return JDPanel.fn(panel -> {
            final var selectedTabIdx = useState(0);
            final Memo<Hook.ApplicationHook> selectedAppSupp = useMemo(() ->
                    selectedTabIdx.get() > hook.get().applicationHooks().size() - 1 ?
                            null : // Fallback tab is selected
                            hook.get().applicationHooks().stream().skip(selectedTabIdx.get()).findFirst().orElse(null));

            panel.visible(visible::get);
            panel.layout(() -> new MigLayout(
                    new LC().wrapAfter(1).fill(),
                    new AC().grow(),
                    new AC().gap().grow()
            ));

            panel.children(panelChildren -> {
                panelChildren.add(JDPanel.fn(infoPanel -> {
                    infoPanel.layout(() -> new MigLayout(
                            new LC().fillX().wrapAfter(1).minWidth("0px"),
                            new AC().grow()
                    ));

                    infoPanel.children(infoPanelChildren -> {
                        infoPanelChildren.add(JDLabel.fn(label -> label.text(() -> "Name: ")), new CC().split(2));
                        infoPanelChildren.add(JDTextField.fn(textField -> {
                            textField.text(() -> device.get().name());
//                                (v, newName) -> v.update(d -> d.withName(newName)) TODO: document filter
                        }), new CC().growX());

                        infoPanelChildren.add(JDLabel.fn(label -> label.text(() -> "ID: " + device.get().id())), new CC().minWidth("0px"));
                        infoPanelChildren.add(JDLabel.fn(label -> label.text(() -> "Description: " + device.get().desc())), new CC().minWidth("0px"));

                    });

                }), new CC().growX());

                panelChildren.add(JDPanel.fn(buttonsPanel -> {
                    buttonsPanel.layout(() -> new MigLayout(
                            new LC().flowY().alignY("top").insetsAll("0"))
                    );

                    buttonsPanel.children(buttonsPanelChildren -> {
                        buttonsPanelChildren.add(JDButton.fn(addApplicationBtn -> {
                            addApplicationBtn.icon(() -> new ImageIcon(
                                    new MultiResolutionIconFont(FontAwesome.PLUS, 14, new Color(0, 150, 0))));
                            addApplicationBtn.margin(() -> new Insets(2, 2, 2, 2));
                            addApplicationBtn.actionListener(evt -> SelectProcessDialog
                                    .selectDevice(SwingUtilities.windowForComponent((Component) evt.getSource()), true)
                                    .thenAccept(process -> SwingUtilities.invokeLater(() -> {
                                        if (process == null)
                                            return;

                                        final Optional<Hook.ApplicationHook> maybeApplicationHook = hook.get().applicationHooks().stream()
                                                .filter(a -> a.application().process().equals(process.name()))
                                                .findFirst();
                                        if(maybeApplicationHook.isPresent()) {
                                            JOptionPane.showMessageDialog(
                                                    SwingUtilities.windowForComponent((Component) evt.getSource()),
                                                    String.format("Process %s was already added as %s",
                                                            process.name(),
                                                            maybeApplicationHook.get().application().name()),
                                                    "Warning",
                                                    JOptionPane.WARNING_MESSAGE);
                                            return;
                                        }

                                        final var applicationHook = new Hook.ApplicationHook(
                                                new Hook.Application(
                                                        process.name(),
                                                        process.name().toLowerCase(Locale.ROOT).endsWith(".exe") ?
                                                                capitalize(process.name().substring(0, process.name().length() - ".exe".length())) :
                                                                capitalize(process.name()),
                                                        process.iconPath()),
                                                Collections.emptyList());
                                        setHook.accept(hook.get().addApplicationHook(applicationHook));
                                    })));
                        }));

                        buttonsPanelChildren.add(JDButton.fn(removeApplicationBtn -> {
                            removeApplicationBtn.icon(() -> new ImageIcon(
                                    new MultiResolutionIconFont(FontAwesome.MINUS, 14, new Color(150, 0, 0))));
                            removeApplicationBtn.margin(() -> new Insets(2, 2, 2, 2));
                            removeApplicationBtn.enabled(() -> selectedAppSupp.get() != null);
                            removeApplicationBtn.actionListener(evt -> {
                                if(selectedAppSupp.get() != null)
                                    setHook.accept(hook.get().removeApplicationHook(selectedAppSupp.get()));
                            });
                        }));
                    });
                }), new CC().growY().split(2));

                panelChildren.add(JDTabbedPane.fn(applicationsPane -> {
                    applicationsPane.name(() -> "ApplicationsTabbedPane");
                    applicationsPane.tabLayoutPolicy(() -> JTabbedPane.SCROLL_TAB_LAYOUT);
                    applicationsPane.tabs(tabs -> {
                        Memo.mapCollection(() -> hook.get().applicationHooks(), (app0, declareAppIdxMemo) -> tabs.addTab(
                                app0.application().process(),
                                app0.application().name(),
                                null,
                                DWrapper.fn(tabComponent -> {
                                    var appIconPath = useMemo(() -> app0.application().icon());
                                    return tabComponent(
                                            useMemo(() -> app0.application().name()),
                                            useMemo(() -> new MultiResolutionIconImage(
                                                    TAB_ICON_SIZE,
                                                    Process.extractProcessIcons(appIconPath.get()))));
                                }),
                                DWrapper.fn(applicationPane -> {
                                    var applicationHook = useMemo(() -> app0);
                                    return JDApplicationPane.fn(
                                            process -> hook.get().applicationHooks().stream()
                                                    .filter(a -> a.application().process().equals(process.name()))
                                                    .findFirst(),
                                            useMemo(() -> applicationHook.get().application()),
                                            application -> setHook.accept(hook.get().replaceApplicationHook(
                                                    applicationHook.get(),
                                                    applicationHook.get().withApplication(application))),
                                            applicationHook,
                                            newApplicationHook -> setHook.accept(hook.get().replaceApplicationHook(
                                                    applicationHook.get(),
                                                    newApplicationHook)));
                                }),
                                app0.application().name()
                        ));

                        tabs.addTab(
                                "Fallback",
                                null,
                                tabComponent(() -> "Fallback", () -> null),
                                DWrapper.fn(wrapper -> JDFallbackPane.fn(
                                        hook.get().fallbackBehavior(),
                                        behavior -> setHook.accept(hook.get().withFallbackBehavior(behavior)))),
                                "Fallback");
                    });
                    applicationsPane.selectedTab(selectedTabIdx);
                    applicationsPane.changeListener(evt ->
                            selectedTabIdx.set(((JTabbedPane) evt.getSource()).getSelectedIndex()));
                }), new CC().grow());
            });
        });
    }

    private static DeclarativeComponent<? extends Container> tabComponent(IdentityFreeSupplier<String> title,
                                                                          IdentityFreeSupplier<Image> imageIn) {
        return JDPanel.fn(() -> new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                // Instead of setting the component opaque, do this
                // otherwise DarkLaf tries to fix a bug with antialiasing and ends up drawing the text with a bg
                // See https://github.com/weisJ/darklaf/blob/69ea0119a1f9f98dadf50e955c8e5e862a3f18b3/core/src/main/java/com/github/weisj/darklaf/graphics/StringPainter.java#L131
            }
        }, tabPanel -> {
            tabPanel.layout(() -> new MigLayout(
                    new LC().flowY().fill().insetsAll("1").gridGap("1", "1")
            ));

            tabPanel.children(tabPanelChildren -> {
                tabPanelChildren.add(JDLabel.fn(iconLabel -> iconLabel.icon(
                                () -> new ImageIcon(imageIn.get() != null ?
                                        imageIn.get() :
                                        createTransparentImage(TAB_ICON_SIZE, TAB_ICON_SIZE)))),
                        new CC().alignX("center"));
                tabPanelChildren.add(JDLabel.fn(nameLabel -> {
                    nameLabel.text(title);
                    nameLabel.maximumWidth(() -> 64);
                }), new CC().alignX("center"));
            });
        });
    }

    private static BufferedImage createTransparentImage(int width, int height) {
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = bufferedImage.createGraphics();

        graphics.setBackground(new Color(0, true));
        graphics.clearRect(0, 0, width, height);
        graphics.dispose();

        return bufferedImage;
    }

    private static String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
