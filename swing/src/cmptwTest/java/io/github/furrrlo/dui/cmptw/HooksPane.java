package io.github.furrrlo.dui.cmptw;

import io.github.furrrlo.dui.*;
import io.github.furrrlo.dui.swing.JDButton;
import io.github.furrrlo.dui.swing.JDComboBox;
import io.github.furrrlo.dui.swing.JDPanel;
import jiconfont.icons.font_awesome.FontAwesome;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static io.github.furrrlo.dui.Hooks.useMemo;
import static io.github.furrrlo.dui.Hooks.useState;

class HooksPane {

    private static final Hook DUMMY_HOOK = new Hook(
            new Hook.Device("", "", ""),
            Collections.emptyList(),
            Hook.FallbackBehavior.IGNORE);

    public static DeclarativeComponent<? extends Container> fn(List<Hook> initialHooks) {
        return JDPanel.fn(panel -> {
            final var hooks = useState(() -> Stream
                    .concat(Stream.of(DUMMY_HOOK), initialHooks.stream())
                    .toList());
            final var selectedIdx = useState(() -> hooks.get().size() - 1);
            final Supplier<Hook> selectedSupp = useMemo(() ->
                    selectedIdx.get() > 0 && selectedIdx.get() < hooks.get().size() ?
                            hooks.get().get(selectedIdx.get()) :
                            DUMMY_HOOK);
            final var anythingEdited = useState(true); // TODO

            panel.layout(() -> new MigLayout(
                    new LC().wrapAfter(1).fill().maxWidth("100%").maxHeight("100%"),
                    new AC().grow().align("center"),
                    new AC().gap().grow().gap().grow(0)
            ));
            panel.name(() -> "panel");
            panel.children(children -> {
                children.add(childProps -> {
                    childProps.comp = JDComboBox.fn(comboBox -> {
                        comboBox.items(() -> hooks.get().stream()
                                .map(h -> h.device().name())
                                .map(s -> s.isEmpty() ? " " : s)
                                .toList());
                        comboBox.selectedItem(() -> selectedSupp.get().device().name().isEmpty() ?
                                " " :
                                selectedSupp.get().device().name());
                        comboBox.actionListener(evt -> {
                            final int newSelectedIdx = ((JComboBox<?>) evt.getSource()).getSelectedIndex();
                            selectedIdx.set(newSelectedIdx);
                        });
                    });
                    childProps.constraints = new CC().growX().split(3);
                });

                children.add(JDButton.fn(addDeviceBtn -> {
                    addDeviceBtn.icon(() -> new ImageIcon(new MultiResolutionIconFont(
                            FontAwesome.PLUS, 14, new Color(0, 150, 0))));
                    addDeviceBtn.margin(() -> new Insets(2, 2, 2, 2));
                    addDeviceBtn.actionListener(evt -> SelectDeviceDialog
                            .selectDevice(SwingUtilities.windowForComponent((Component) evt.getSource()))
                            .thenAccept(device -> SwingUtilities.invokeLater(() -> {
                                if (device == null)
                                    return;

                                final Optional<Hook> maybeHook = hooks.get().stream()
                                        .filter(d -> d.device().id().equals(device.getId()))
                                        .findFirst();
                                if (maybeHook.isPresent()) {
                                    JOptionPane.showMessageDialog(
                                            SwingUtilities.windowForComponent((Component) evt.getSource()),
                                            String.format("Device was already added as \"%s\"", maybeHook.get().device().name()),
                                            "Warning",
                                            JOptionPane.WARNING_MESSAGE);
                                    return;
                                }

                                final var hook = new Hook(
                                        new Hook.Device(device.getId(), device.getDesc(), device.getDesc()),
                                        Collections.emptyList(),
                                        Hook.FallbackBehavior.IGNORE);
                                hooks.update(curr -> {
                                    final var n = new ArrayList<>(curr);
                                    n.add(hook);
                                    return n;
                                });
                                selectedIdx.set(hooks.get().size() - 1);
                            })));
                }));

                children.add(JDButton.fn(removeDeviceBtn -> {
                    removeDeviceBtn.icon(() -> new ImageIcon(new MultiResolutionIconFont(
                            FontAwesome.MINUS, 14, new Color(150, 0, 0))));
                    removeDeviceBtn.actionListener(evt -> {
                        var selected = selectedSupp.get();
                        if (selected == null)
                            return;

                        hooks.update(curr -> {
                            final var n = new ArrayList<>(curr);
                            n.remove(selected);
                            return n;
                        });
                    });
                    removeDeviceBtn.margin(() -> new Insets(2, 2, 2, 2));
                    removeDeviceBtn.enabled(() -> !selectedSupp.get().equals(DUMMY_HOOK));
                }));

                children.add(currentDevicePanel(hooks, selectedSupp), new CC().grow().pushY());

                children.add(JDButton.fn(applyBtn -> {
                    applyBtn.text(() -> "Apply");
                    applyBtn.enabled(anythingEdited);
                    applyBtn.actionListener(evt -> anythingEdited.set(false));
                }), new CC().tag("apply").split(2));

                children.add(JDButton.fn(cancelBtn -> {
                    cancelBtn.name(() -> "cancel_btn");
                    cancelBtn.text(() -> "Cancel");
                    cancelBtn.actionListener(evt -> {
                        if (anythingEdited.get())
                            hooks.set(Stream
                                    .concat(Stream.of(DUMMY_HOOK), initialHooks.stream())
                                    .toList());
                    });
                }), new CC().tag("cancel"));
            });
        });
    }

    private static DeclarativeComponent<? extends JComponent> currentDevicePanel(
            State<List<Hook>> hooks,
            Supplier<Hook> selectedSupp
    ) {
        return JDPanel.fn(currentPanel -> {
            currentPanel.name(() -> "currentPanel");
            currentPanel.layout(() -> new MigLayout(
                    new LC().fill().insetsAll("0").hideMode(3),
                    new AC().align("center"),
                    new AC().align("center")
            ));

            currentPanel.children(currentPanelChildren -> Memo.mapCollection(hooks::get, (hook0, declareIndexMemo) ->
                    currentPanelChildren.add(hook0.device().id(), DWrapper.fn(hookPanel -> {
                        final var hookIndex = declareIndexMemo.get();
                        // Turn the hook itself into a signal to propagate reactivity
                        final var hook = useMemo(() -> hook0);
//                               TODO:
//                                If it's the dummy one, disable everything
//                                if(hookIn == dummyHook) {
//                                    final Deque<Component> components = new LinkedList<>(List.of(panel));
//                                    while(!components.isEmpty()) {
//                                        final var component = components.pop();
//                                        component.setEnabled(false);
//                                        if(component instanceof Container container)
//                                            Collections.addAll(components, container.getComponents());
//                                    }
//                                }
                        return DevicePanel.fn(props -> {
                            props.visible = hook.get().equals(selectedSupp.get());
                            props.hook = hook.get();
                            props.setHook = newHook -> hooks.update(l -> {
                                final var newHooks = new ArrayList<>(l);
                                if(hookIndex.get() != -1)
                                    newHooks.set(hookIndex.get(), newHook);
                                return newHooks;
                            });
                            props.device = hook.get().device();
                            props.setDevice = newDevice -> hooks.update(l -> {
                                final var newHooks = new ArrayList<>(l);
                                final var idx = hookIndex.get();
                                if(idx != -1)
                                    newHooks.set(idx, newHooks.get(idx).withDevice(newDevice));
                                return newHooks;
                            });
                        });
                    }), new CC().grow())));
        });
    }
}
