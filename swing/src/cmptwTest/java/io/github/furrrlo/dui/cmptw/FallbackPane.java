package io.github.furrrlo.dui.cmptw;

import io.github.furrrlo.dui.DeclarativeComponent;
import io.github.furrrlo.dui.DeclarativeComponentSupplier;
import io.github.furrrlo.dui.Memo;
import io.github.furrrlo.dui.swing.JDLabel;
import io.github.furrrlo.dui.swing.JDPanel;
import io.github.furrrlo.dui.swing.JDRadioButton;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static io.github.furrrlo.dui.Hooks.useMemo;

class FallbackPane {

    public static DeclarativeComponent<? extends Component> fn(Hook.FallbackBehavior fallbackBehavior,
                                                               Consumer<Hook.FallbackBehavior> setFallbackBehavior) {
        return JDPanel.fn(panel -> {
            final Memo<ButtonGroup> buttonGroup = useMemo(ButtonGroup::new);

            panel.layout(() -> new MigLayout(new LC().wrapAfter(1).fillX()));
            panel.children(panelChildren -> {
                panelChildren.add(JDLabel.fn(label -> label.text(() -> "When a key stroke is not matched by any hook:")));

                final BiFunction<Hook.FallbackBehavior, String, DeclarativeComponentSupplier<? extends Component>>
                        createBehaviorBtn = (behavior, text) -> JDRadioButton.fn(btn -> {
                    btn.text(() -> text);
                    btn.selected(() -> behavior.equals(fallbackBehavior));
                    btn.actionListener(evt -> setFallbackBehavior.accept(behavior));
                    btn.buttonGroup(buttonGroup);
                });

                panelChildren.add(createBehaviorBtn.apply(Hook.FallbackBehavior.IGNORE, "Let it through"));
                panelChildren.add(createBehaviorBtn.apply(Hook.FallbackBehavior.DELETE, "Block it"));
                panelChildren.add(createBehaviorBtn.apply(Hook.FallbackBehavior.DELETE_AND_PLAY_SOUND,
                        "Block it and play error sound"));
            });
        });
    }
}
