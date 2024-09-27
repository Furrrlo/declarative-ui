package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.Application;
import io.github.furrrlo.dui.ApplicationConfig;
import io.github.furrrlo.dui.Hooks;
import io.github.furrrlo.dui.assertj.swing.AssertJSwingJUnit5TestCase;
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

class CounterTest extends AssertJSwingJUnit5TestCase {
    @Override
    protected void onSetUp() {
    }

    @Test
    void counter() throws Exception {
        var title = "CounterTest#counter(): ";

        var cfg = ApplicationConfig.builder()
                .grantAccess(MethodHandles.lookup())
                .build();
        Application.create(cfg, app -> app.roots(roots -> roots.add(JDFrame.fn(frame -> {
            var counter = useState(0);
            var frameRef = Hooks.<JFrame>useThrowingRef("Missing main frame");

            useSideEffect(() -> {
                frameRef.curr().pack();
                frameRef.curr().setLocationRelativeTo(null);
            });

            frame.ref(frameRef);
            frame.title(() -> title + counter.get());
            frame.contentPane(JDPanel.fn(panel -> {
                panel.layout(FlowLayout::new);
                panel.children(children -> {
                    children.add(JDLabel.fn(label -> label.text(() -> "Current value is " + counter.get())));
                    children.add(JDButton.fn(btn -> {
                        btn.name(() -> "Click me");
                        btn.text(() -> "Click me");
                        btn.actionListener(evt -> counter.update(i -> i + 1));
                    }));
                });
            }));
            frame.visible(() -> true);
        }))));

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
            window.button("Click me").click();
            window.button("Click me").click();
            window.button("Click me").click();
            window.requireTitle(title + "3");
        }
    }
}
