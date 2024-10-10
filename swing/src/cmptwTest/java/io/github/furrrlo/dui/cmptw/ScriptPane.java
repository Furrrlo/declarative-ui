package io.github.furrrlo.dui.cmptw;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.components.loading.LoadingIndicator;
import com.github.weisj.darklaf.components.text.NumberedTextComponent;
import com.github.weisj.darklaf.extensions.rsyntaxarea.DarklafRSyntaxTheme;
import com.github.weisj.darklaf.theme.event.ThemeInstalledListener;
import io.github.furrrlo.dui.DWrapper;
import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentSupplier;
import io.github.furrrlo.dui.swing.*;
import jiconfont.icons.font_awesome.FontAwesome;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static io.github.furrrlo.dui.Hooks.*;

class ScriptPane {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptPane.class);
    private static final ExecutorService VALIDATION_EXECUTOR = Executors.newSingleThreadExecutor(r -> {
        final Thread th = Executors.defaultThreadFactory().newThread(r);
        th.setName("script-validation-thread");
        th.setDaemon(true);
        th.setUncaughtExceptionHandler((t, e) -> LOGGER.error("Uncaught exception in script validation thread", e));
        return th;
    });
    private static @Nullable Theme syntaxTheme;

    public static DeclarativeComponent<? extends Component> fn(
            Supplier<Hook.HookScript> script,
            Consumer<Hook.HookScript> setScript
    ) {
        return JDPanel.fn(panel -> {
            final var isValidating = useState(false);
            final var isValid = useState(true);

            panel.layout(() -> new MigLayout(
                    new LC().wrapAfter(1).fill(),
                    new AC().grow(),
                    new AC().gap().gap().grow()
            ));

            panel.children(panelChildren -> {
                panelChildren.add(infoPanel(script, setScript), new CC().growX());
                panelChildren.add(JDLabel.fn(label -> label.text(() -> "Script: ")), new CC().split(3).growX());
                panelChildren.add(JDLabel.fn(LoadingIndicator.class, LoadingIndicator::new, validatingIndicator -> {
                    validatingIndicator.enabled(isValidating);
                    validatingIndicator.attribute(
                            "LoadingIndicator_running",
                            LoadingIndicator::setRunning,
                            isValidating);
                }));

                panelChildren.add(JDButton.fn(validateBtn -> {
                    final var icon = useMemo(() -> Map.of(
                            true, new ImageIcon(new MultiResolutionIconFont(
                                    FontAwesome.WRENCH, 14,
                                    Color.GREEN.darker())),
                            false, new ImageIcon(new MultiResolutionIconFont(
                                    FontAwesome.WRENCH, 14,
                                    Color.RED.darker()))
                    ));

                    validateBtn.enabled(() -> !isValidating.get());
                    validateBtn.icon(() -> icon.get().get(isValid.get()));
                    validateBtn.margin(() -> new Insets(2, 2, 2, 2));
                    validateBtn.toolTipText(() -> "Validate");
                    validateBtn.actionListener(evt -> {
                        isValidating.set(true);

                        VALIDATION_EXECUTOR.submit(() -> {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException("Validating", e);
                            } finally {
                                isValid.set(true);
                                isValidating.set(false);
                            }
                        });
                    });
                }));

                panelChildren.add(scriptTextArea(script), new CC().grow());
            });
        });
    }

    private static DeclarativeComponentSupplier<? extends Component> infoPanel(
            Supplier<Hook.HookScript> script,
            Consumer<Hook.HookScript> setScript
    ) {
        return JDPanel.fn(infoPanel -> {
            infoPanel.layout(() -> new MigLayout(
                    new LC().fillX().wrapAfter(2).insetsAll("0"),
                    new AC().gap().grow()
            ));

            infoPanel.children(infoPanelChildren -> {

                infoPanelChildren.add(JDLabel.fn(label -> label.text(() -> "Name: ")));
                infoPanelChildren.add(JDTextField.fn(textField -> {
                    textField.text(() -> script.get().name());
                    textField.textChangeListener(evt ->
                            setScript.accept(script.get().withName(evt.getNewTextOr(""))));
                }), new CC().growX());

                infoPanelChildren.add(JDLabel.fn(label -> label.text(() -> "Key stroke: ")));

                infoPanelChildren.add(JDTextField.fn(keyStrokeField -> {
                    keyStrokeField.text(() -> script.get().keyStroke().getText(" + "));
                    keyStrokeField.editable(() -> false);
                }), new CC().growX().split(2));

                infoPanelChildren.add(JDButton.fn(keyStrokeButton -> {
                    keyStrokeButton.text(() -> "Change");
                    keyStrokeButton.actionListener(evt -> SelectKeyStrokeDialog
                            .selectKeyStroke(
                                    SwingUtilities.windowForComponent((Component) evt.getSource()),
                                    null,  // TODO: target device
                                    script.get().keyStroke().toggleModifiersMask())
                            .thenAccept(res -> {
                                if (res != null)
                                    setScript.accept(script.get().withKeyStroke(new Hook.KeyStroke(
                                            res.evt().awtKeyCode(), res.evt().modifiers(), res.toggleKeysMask())));
                            }));
                }));

                infoPanelChildren.add(JDLabel.fn(label -> label.text(() -> "Match toggle keys: ")));
                final BiFunction<Integer, String, DeclarativeComponentSupplier<? extends Component>> makeToggleKeyCheckBox =
                        (modifier, text) -> JDCheckBox.fn(checkBox -> {
                            checkBox.text(() -> text);
                            checkBox.selected(() -> (script.get().keyStroke().toggleModifiersMask() & modifier) != 0);
                            checkBox.actionListener(evt -> {
                                final boolean bool = ((JCheckBox) evt.getSource()).isSelected();
                                setScript.accept(script.get().withKeyStroke(script.get().keyStroke().updateToggleModifiersMask(mask -> {
                                    if (bool)
                                        return mask | modifier;
                                    return mask & ~modifier;
                                })));
                            });
                        });
                infoPanelChildren.add(makeToggleKeyCheckBox.apply(KeyboardHookEvent.CAPS_LOCK_MASK, "Caps Lock"),
                        new CC().split(3));
                infoPanelChildren.add(makeToggleKeyCheckBox.apply(KeyboardHookEvent.NUM_LOCK_MASK, "Num Lock"));
                infoPanelChildren.add(makeToggleKeyCheckBox.apply(KeyboardHookEvent.SCROLL_LOCK_MASK, "Scroll Lock"));

            });
        });
    }

    private static DeclarativeComponentSupplier<? extends Component> scriptTextArea(
            Supplier<Hook.HookScript> script
    ) {
        return DWrapper.fn(ctx -> {
            final var isLafManagerInstalled = useState(LafManager::isInstalled);

            useDisposableEffect(scope -> {
                var installListener = (ThemeInstalledListener) evt -> isLafManagerInstalled.set(true);
                LafManager.addThemeChangeListener(installListener);
                scope.onDispose(() -> LafManager.removeThemeChangeListener(installListener));
            });

            final var wrappedTextArea = DRSyntaxTextArea.fn(textArea -> {
                textArea.autoCompletion(() -> Optional.of(new DefaultCompletionProvider())
                        .map(provider -> {
                            final AutoCompletion autoCompletion = new AutoCompletion(provider);
                            autoCompletion.setTriggerKey(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.ALT_DOWN_MASK));
                            autoCompletion.setAutoCompleteEnabled(true);
                            autoCompletion.setAutoCompleteSingleChoices(false);
                            autoCompletion.setAutoActivationEnabled(true);
                            autoCompletion.setShowDescWindow(true);
                            return autoCompletion;
                        })
                        .orElse(null));
                textArea.syntaxEditingStyle(() -> "text/ahk");
                textArea.codeFoldingEnabled(() -> true);
                textArea.text(() -> script.get().script());
                if(isLafManagerInstalled.get())
                    textArea.theme(() -> syntaxTheme);
                // TODO: listener
            });

            return isLafManagerInstalled.get() ?
                    JDPanel.fn(
                            () -> {
                                var syntaxTheme1 = ScriptPane.syntaxTheme;
                                if (syntaxTheme1 == null)
                                    ScriptPane.syntaxTheme = syntaxTheme1 = new DarklafRSyntaxTheme();

                                var textArea = new RSyntaxTextArea();
                                syntaxTheme1.apply(textArea);
                                return new NumberedTextComponent(textArea);
                            },
                            p -> p.inner(
                                    o -> (RSyntaxTextArea) ((NumberedTextComponent) o).getTextComponent(),
                                    wrappedTextArea)) :
                    // When it's created without a textArea it looks weird
                    DRTextScrollPane.fn(
                            () -> new RTextScrollPane(new RSyntaxTextArea()),
                            p -> p.inner(o -> (RSyntaxTextArea) o.getTextArea(), wrappedTextArea));
        });
    }
}
