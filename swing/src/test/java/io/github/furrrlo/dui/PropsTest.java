package io.github.furrrlo.dui;

import io.github.furrrlo.dui.assertj.swing.AssertJSwingJUnit5TestCase;
import io.github.furrrlo.dui.swing.JDButton;
import io.github.furrrlo.dui.swing.JDFrame;
import io.github.furrrlo.dui.swing.JDPanel;
import io.github.furrrlo.dui.swing.JDToggleButton;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.furrrlo.dui.Hooks.*;
import static org.assertj.swing.finder.WindowFinder.findFrame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PropsTest extends AssertJSwingJUnit5TestCase {

    @Override
    protected void onSetUp() {
    }

    static class Props {
        public boolean bool1;
        public boolean bool2;

        private Props(IdentityFreeConsumer<Props> propsFn) {
            propsFn.accept(this);
        }
    }

    static DeclarativeComponent<? extends Component> componentUsingProps(IdentityFreeConsumer<Props> propsFn) {
        return JDPanel.fn(panel -> {
            var props = useProps(propsFn, Props::new);

            panel.children(children -> {
                children.add(JDToggleButton.fn(btn -> {
                    var ref = useRef((JToggleButton) null);
                    btn.ref(ref);
                    btn.name(() -> "bool1");
                    btn.selected(() -> {
                        var actualBtn = ref.curr();
                        if(actualBtn != null) {
                            var updates = actualBtn.getClientProperty("updates");
                            actualBtn.putClientProperty(
                                    "updates",
                                    updates instanceof Integer num ? num + 1 : 0);
                        }
                        return props.map(p -> p.bool1);
                    });
                }));

                children.add(JDToggleButton.fn(btn -> {
                    var ref = useRef((JToggleButton) null);
                    btn.ref(ref);
                    btn.name(() -> "bool2");
                    btn.selected(() -> {
                        var actualBtn = ref.curr();
                        if(actualBtn != null) {
                            var updates = actualBtn.getClientProperty("updates");
                            actualBtn.putClientProperty(
                                    "updates",
                                    updates instanceof Integer num ? num + 1 : 0);
                        }
                        return props.map(p -> p.bool2);
                    });
                }));
            });
        });
    }

    @Test
    void testUpdates() throws Exception {
        var title = "PropsTest#testUpdates()";

        Application.create(app -> {
            grantAccess(MethodHandles::lookup);

            app.roots(roots -> roots.add(JDFrame.fn(frame -> {
                var frameRef = Hooks.<JFrame>useThrowingRef("Missing top-level frame");
                useSideEffect(() -> {
                    frameRef.curr().pack();
                    frameRef.curr().setLocationRelativeTo(null);
                });

                frame.ref(frameRef);
                frame.title(() -> title);
                frame.contentPane(JDPanel.fn(contentPane -> {
                    var bool1 = useState(false);
                    var bool2 = useState(false);

                    contentPane.children(children -> {
                        children.add(JDButton.fn(btn -> {
                            btn.name(() -> "toggleBool1");
                            btn.actionListener(evt -> bool1.update(b -> !b));
                        }));

                        children.add(JDButton.fn(btn -> {
                            btn.name(() -> "toggleBool2");
                            btn.actionListener(evt -> bool2.update(b -> !b));
                        }));

                        children.add(componentUsingProps(props -> {
                            props.bool1 = bool1.get();
                            props.bool2 = bool2.get();
                        }));
                    });
                }));
                frame.visible(() -> true);
            })));
        });

        var windowRef = new AtomicReference<FrameFixture>();
        try(AutoCloseable windowCloseable = () -> windowRef.get().cleanUp()) {
            @SuppressWarnings("unused") var unused = windowCloseable;
            windowRef.set(
                    findFrame(new GenericTypeMatcher<>(Frame.class) {
                        @Override
                        protected boolean isMatching(Frame frame) {
                            return frame.getTitle().startsWith(title) && frame.isShowing();
                        }
                    }).withTimeout(1, TimeUnit.MINUTES).using(robot())
            );

            var window = windowRef.get();
            assertNotNull(window);

            int numOfUpdates = 10;
            for(int i = 0; i < numOfUpdates; i++) {
                window.button("toggleBool1").click();

                if(i % 2 == 0)
                    window.toggleButton("bool1").requireSelected();
                else
                    window.toggleButton("bool1").requireNotSelected();
                assertEquals(
                        i + 1,
                        window.toggleButton("bool1").target().getClientProperty("updates"),
                        "iter " + i);

                window.toggleButton("bool2").requireNotSelected();
            }

            assertEquals(
                    0,
                    window.toggleButton("bool2").target().getClientProperty("updates"));

            for(int i = 0; i < numOfUpdates; i++) {
                window.button("toggleBool2").click();

                if(i % 2 == 0)
                    window.toggleButton("bool2").requireSelected();
                else
                    window.toggleButton("bool2").requireNotSelected();
                assertEquals(
                        i + 1,
                        window.toggleButton("bool2").target().getClientProperty("updates"),
                        "iter " + i);

                window.toggleButton("bool1").requireNotSelected();
            }

            assertEquals(
                    numOfUpdates,
                    window.toggleButton("bool1").target().getClientProperty("updates"));
        }
    }
}
