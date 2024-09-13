package io.github.furrrlo.dui.cmptw;

import io.github.furrrlo.dui.DWrapper;
import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.Memo;
import io.github.furrrlo.dui.Ref;
import io.github.furrrlo.dui.swing.*;
import jiconfont.icons.font_awesome.FontAwesome;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static io.github.furrrlo.dui.Hooks.*;

class JDApplicationPane {

    public static DeclarativeComponent<? extends Component> fn(Function<Process, Optional<Hook.ApplicationHook>> getApplicationHookFor,
                                                               Supplier<Hook.Application> application,
                                                               Consumer<Hook.Application> setApplication,
                                                               Supplier<Hook.ApplicationHook> applicationHook,
                                                               Consumer<Hook.ApplicationHook> setApplicationHook) {
        return JDPanel.fn(panel -> {
            final var selectedScriptIdx = useState(() -> applicationHook.get().scripts().isEmpty() ? -1 : 0);
            final Supplier<Hook.HookScript> selectedScriptSupp = useMemo(() -> selectedScriptIdx.get() < 0 ?
                    null :
                    applicationHook.get().scripts().stream().skip(selectedScriptIdx.get()).findFirst().orElse(null));
            final Ref<JPanel> panelRef = useThrowingRef("Missing application pane");

            panel.ref(panelRef);
            panel.layout(() -> new MigLayout(
                    new LC().wrapAfter(1).fill(),
                    new AC().grow(),
                    new AC().gap().grow()
            ));

            panel.children(panelChildren -> {
                panelChildren.add(JDPanel.fn(infoPanel -> {
                    infoPanel.layout(() -> new MigLayout(
                            new LC().fillX().wrapAfter(2),
                            new AC().gap().grow()
                    ));
                    infoPanel.children(infoPanelChildren -> {
                        infoPanelChildren.add(JDLabel.fn(label -> label.text(() -> "Name: ")));
                        infoPanelChildren.add(JDTextField.fn(textField -> {
                            textField.text(() -> application.get().name());
//                                (v, newName) -> v.update(a -> a.withName(newName)) TODO:
                        }), new CC().growX());

                        infoPanelChildren.add(JDLabel.fn(label -> label.text(() -> "Process: ")));
                        infoPanelChildren.add(JDTextField.fn(textField -> {
                            textField.text(() -> application.get().process());
//                                (v, newProcess) -> v.update(a -> a.withProcess(newProcess)) TODO:
                        }), new CC().growX().split(2));

                        infoPanelChildren.add(JDButton.fn(selectProcessBtn -> {
                            selectProcessBtn.text(() -> "...");
                            selectProcessBtn.actionListener(evt -> SelectProcessDialog
                                    .selectDevice(SwingUtilities.windowForComponent((Component) evt.getSource()), true)
                                    .thenAccept(process -> SwingUtilities.invokeLater(() -> {
                                        if (process == null)
                                            return;

                                        final Optional<Hook.ApplicationHook> maybeApplicationHook = getApplicationHookFor.apply(process);
                                        if (maybeApplicationHook.isPresent()) {
                                            JOptionPane.showMessageDialog(
                                                    SwingUtilities.windowForComponent((Component) evt.getSource()),
                                                    String.format("Process %s was already added as %s",
                                                            process.name(),
                                                            maybeApplicationHook.get().application().name()),
                                                    "Warning",
                                                    JOptionPane.WARNING_MESSAGE);
                                            return;
                                        }

                                        setApplication.accept(application.get().withProcess(process.name()));
                                    })));
                        }));

                        infoPanelChildren.add(JDLabel.fn(label -> label.text(() -> "Icon: ")));
                        infoPanelChildren.add(JDTextField.fn(textField -> {
                            textField.text(() -> application.get().icon().toAbsolutePath().toString());
//                                (v, newIcon) -> v.update(a -> a.withIcon(Path.of(newIcon)))
                        }), new CC().growX().split(2));

                        infoPanelChildren.add(JDButton.fn(browseIconBtn -> {
                            browseIconBtn.text(() -> "...");
                            browseIconBtn.actionListener(evt -> {
                                final var chooser = new JFileChooser();
                                chooser.addChoosableFileFilter(new FileNameExtensionFilter(
                                        String.format("Icons (%s)", Process.ICON_EXTENSIONS.stream()
                                                .map(ext -> "*." + ext)
                                                .collect(Collectors.joining(", "))),
                                        Process.ICON_EXTENSIONS.toArray(String[]::new)));
                                chooser.setAcceptAllFileFilterUsed(true);
                                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                                chooser.setMultiSelectionEnabled(false);

                                final var res = chooser.showOpenDialog(panelRef.curr());
                                if (res != JFileChooser.APPROVE_OPTION)
                                    return;

                                final File newIcon = chooser.getSelectedFile().getAbsoluteFile();
                                if (Objects.equals(newIcon.toPath(), application.get().icon().toAbsolutePath()))
                                    return;

                                setApplication.accept(application.get().withIcon(newIcon.toPath()));
                            });
                        }));
                    });
                }), new CC().growX());

                panelChildren.add(JDPanel.fn(buttonsPanel -> {
                    buttonsPanel.layout(() -> new MigLayout(
                            new LC().flowY().alignY("top").insetsAll("0")
                    ));

                    buttonsPanel.children(buttonsPanelChildren -> {
                        buttonsPanelChildren.add(JDButton.fn(addScriptBtn -> {
                            addScriptBtn.icon(() -> new ImageIcon(
                                    new MultiResolutionIconFont(FontAwesome.PLUS, 14, new Color(0, 150, 0))));
                            addScriptBtn.margin(() -> new Insets(2, 2, 2, 2));
                            addScriptBtn.actionListener(evt -> SelectKeyStrokeDialog
                                    .selectKeyStroke(SwingUtilities.windowForComponent((Component) evt.getSource()), null, 0) // TODO: target device
                                    .thenAccept(res -> SwingUtilities.invokeLater(() -> {
                                        if (res == null)
                                            return;

                                        final var script = new Hook.HookScript(
                                                "New",
                                                new Hook.KeyStroke(res.evt().awtKeyCode(), res.evt().modifiers(), res.toggleKeysMask()),
                                                "");
                                        setApplicationHook.accept(applicationHook.get().addScript(script));
                                    })));
                        }));

                        buttonsPanelChildren.add(JDButton.fn(removeScriptBtn -> {
                            removeScriptBtn.icon(() -> new ImageIcon(
                                    new MultiResolutionIconFont(FontAwesome.MINUS, 14, new Color(150, 0, 0))));
                            removeScriptBtn.margin(() -> new Insets(2, 2, 2, 2));
                            removeScriptBtn.enabled(() -> selectedScriptSupp.get() != null);
                            removeScriptBtn.actionListener(evt -> {
                                if(selectedScriptSupp.get() != null)
                                    setApplicationHook.accept(applicationHook.get().removeScript(selectedScriptSupp.get()));
                            });
                        }));
                    });
                }), new CC().growY().split(2));

                panelChildren.add(JDTabbedPane.fn(scriptsPane -> {
                    scriptsPane.tabLayoutPolicy(() -> JTabbedPane.SCROLL_TAB_LAYOUT);
                    scriptsPane.tabPlacement(() -> JTabbedPane.LEFT);
                    scriptsPane.tabs(tabs -> Memo.mapCollection(() -> applicationHook.get().scripts(),
                            (script0, declareScriptIdxMemo) -> tabs.addTab(
                                    script0.scriptFile() != null
                                            ? script0.scriptFile().toAbsolutePath().toString()
                                            : script0.name(),
                                    script0.name(),
                                    null,
                                    null,
                                    DWrapper.fn(scriptPane -> {
                                        final Memo<Hook.HookScript> scriptMemo = useMemo(() -> script0);
                                        return JDScriptPane.fn(
                                                scriptMemo,
                                                newScript -> setApplicationHook.accept(
                                                        applicationHook.get().replaceScript(scriptMemo.get(), newScript)));
                                    }),
                                    script0.name())));
                    scriptsPane.selectedTab(selectedScriptIdx);
                    scriptsPane.changeListener(
                            evt -> selectedScriptIdx.set(((JTabbedPane) evt.getSource()).getSelectedIndex()));
                }), new CC().grow());
            });
        });
    }
}
