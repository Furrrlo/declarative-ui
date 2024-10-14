package io.github.furrrlo.dui.cmptw;

import io.github.furrrlo.dui.*;
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

import static io.github.furrrlo.dui.Hooks.*;

class DevicePanel extends JPanel {

    protected static final int TAB_ICON_SIZE = 32;

    @SuppressWarnings("NotNullFieldNotInitialized")
    static class Props {
        public boolean visible;
        public Hook hook;
        public Consumer<Hook> setHook;
        public Hook.Device device;
        public Consumer<Hook.Device> setDevice;

        private Props(IdentityFreeConsumer<Props> propsFn) {
            propsFn.accept(this);
        }
    }

    public static DeclarativeComponent<? extends Container> fn(IdentityFreeConsumer<Props> propsFn) {
        return JDPanel.fn(panel -> {
            final var props = useProps(propsFn, Props::new);
            final var selectedTabIdx = useState(0);
            final Memo<Hook.ApplicationHook> selectedAppSupp = useMemo(() ->
                    selectedTabIdx.get() > props.map(p -> p.hook).applicationHooks().size() - 1 ?
                            null : // Fallback tab is selected
                            props.map(p -> p.hook).applicationHooks().stream().skip(selectedTabIdx.get()).findFirst().orElse(null));

            panel.visible(() -> props.map(p -> p.visible));
            panel.layout(() -> new MigLayout(
                    new LC().wrapAfter(1).fill(),
                    new AC().grow(),
                    new AC().gap().grow()
            ));

            panel.children(panelChildren -> {
                panelChildren.add(infoPanel(props), new CC().growX());
                panelChildren.add(buttonsPanel(props, selectedAppSupp), new CC().growY().split(2));
                panelChildren.add(applicationsTabbedPane(props, selectedTabIdx), new CC().grow());
            });
        });
    }

    private static DeclarativeComponent<? extends JComponent> buttonsPanel(
            SafeMemo<Props> props,
            Memo<Hook.ApplicationHook> selectedAppSupp
    ) {
        return JDPanel.fn(buttonsPanel -> {
            buttonsPanel.layout(() -> new MigLayout(
                    new LC().flowY().alignY("top").insetsAll("0"))
            );

            buttonsPanel.children(buttonsPanelChildren -> {
                buttonsPanelChildren.add(JDButton.fn(addApplicationBtn -> {
                    addApplicationBtn.name(() -> "add_application_btn");
                    addApplicationBtn.icon(() -> new ImageIcon(
                            new MultiResolutionIconFont(FontAwesome.PLUS, 14, new Color(0, 150, 0))));
                    addApplicationBtn.margin(() -> new Insets(2, 2, 2, 2));
                    addApplicationBtn.actionListener(evt -> SelectProcessDialog
                            .selectDevice(SwingUtilities.windowForComponent((Component) evt.getSource()), true)
                            .thenAccept(process -> SwingUtilities.invokeLater(() -> {
                                if (process == null)
                                    return;

                                final Optional<Hook.ApplicationHook> maybeApplicationHook = props
                                        .map(p -> p.hook)
                                        .applicationHooks()
                                        .stream()
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
                                props.accept(
                                        p -> p.setHook,
                                        props.map(p -> p.hook).addApplicationHook(applicationHook));
                            })));
                }));

                buttonsPanelChildren.add(JDButton.fn(removeApplicationBtn -> {
                    removeApplicationBtn.name(() -> "remove_application_btn");
                    removeApplicationBtn.icon(() -> new ImageIcon(
                            new MultiResolutionIconFont(FontAwesome.MINUS, 14, new Color(150, 0, 0))));
                    removeApplicationBtn.margin(() -> new Insets(2, 2, 2, 2));
                    removeApplicationBtn.enabled(() -> selectedAppSupp.get() != null);
                    removeApplicationBtn.actionListener(evt -> {
                        if(selectedAppSupp.get() != null)
                            props.map(p -> p.setHook).accept(
                                    props.map(p -> p.hook).removeApplicationHook(selectedAppSupp.get()));
                    });
                }));
            });
        });
    }

    private static DeclarativeComponent<? extends JComponent> infoPanel(SafeMemo<Props> props) {
        return JDPanel.fn(infoPanel -> {
            infoPanel.layout(() -> new MigLayout(
                    new LC().fillX().wrapAfter(1).minWidth("0px"),
                    new AC().grow()
            ));

            infoPanel.children(infoPanelChildren -> {
                infoPanelChildren.add(JDLabel.fn(label -> label.text(() -> "Name: ")), new CC().split(2));
                infoPanelChildren.add(JDTextField.fn(textField -> {
                    textField.text(() -> props.map(p -> p.device.name()));
                    textField.textChangeListener(evt -> props.accept(
                            p -> p.setDevice,
                            props.map(p -> p.device).withName(evt.getNewTextOr(""))));
                }), new CC().growX());

                infoPanelChildren.add(
                        JDLabel.fn(label -> label.text(() -> "ID: " + props.map(p -> p.device.id()))),
                        new CC().minWidth("0px"));
                infoPanelChildren.add(
                        JDLabel.fn(label -> label.text(() -> "Description: " + props.map(p -> p.device.desc()))),
                        new CC().minWidth("0px"));

            });
        });
    }

    private static DeclarativeComponent<? extends JComponent> applicationsTabbedPane(
            SafeMemo<Props> props,
            State<Integer> selectedTabIdx
    ) {
        return JDTabbedPane.fn(applicationsPane -> {
            applicationsPane.name(() -> "ApplicationsTabbedPane");
            applicationsPane.tabLayoutPolicy(() -> JTabbedPane.SCROLL_TAB_LAYOUT);
            applicationsPane.tabs(tabs -> {
                Memo.mapCollection(() -> props.map(p -> p.hook).applicationHooks(), (applicationHook, declareAppIdxMemo) -> tabs.addTab(
                        applicationHook.application().process(),
                        applicationHook.application().name(),
                        null,
                        DWrapper.fn(tabComponent -> {
                            var appIconPath = useMemo(() -> applicationHook.application().icon());
                            return tabComponent(
                                    useMemo(() -> applicationHook.application().name()),
                                    useMemo(() -> new MultiResolutionIconImage(
                                            TAB_ICON_SIZE,
                                            Process.extractProcessIcons(appIconPath.get()))));
                        }),
                        ApplicationPane.fn(p -> {
                            p.getApplicationHookFor = process -> props.map(p0 -> p0.hook).applicationHooks().stream()
                                    .filter(a -> a.application().process().equals(process.name()))
                                    .findFirst();
                            p.application = applicationHook.application();
                            p.setApplication = application -> props.map(p0 -> p0.setHook).accept(
                                    props.map(p0 -> p0.hook).replaceApplicationHook(
                                            applicationHook,
                                            applicationHook.withApplication(application)));
                            p.applicationHook = applicationHook;
                            p.setApplicationHook = newApplicationHook -> props.map(p0 -> p0.setHook).accept(
                                    props.map(p0 -> p0.hook).replaceApplicationHook(
                                            applicationHook,
                                            newApplicationHook));
                        }),
                        applicationHook.application().name()
                ));

                tabs.addTab(
                        "Fallback",
                        null,
                        tabComponent(() -> "Fallback", () -> null),
                        DWrapper.fn(wrapper -> FallbackPane.fn(
                                props.map(p0 -> p0.hook).fallbackBehavior(),
                                behavior -> props.map(p0 -> p0.setHook).accept(
                                        props.map(p0 -> p0.hook).withFallbackBehavior(behavior)))),
                        "Fallback");
            });
            applicationsPane.selectedTab(selectedTabIdx);
            applicationsPane.changeListener(evt ->
                    selectedTabIdx.set(((JTabbedPane) evt.getSource()).getSelectedIndex()));
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
