package io.github.furrrlo.dui;

import io.github.furrrlo.dui.assertj.swing.AssertJSwingJUnit5TestCase;
import io.github.furrrlo.dui.swing.JDButton;
import io.github.furrrlo.dui.swing.JDFrame;
import io.github.furrrlo.dui.swing.JDPanel;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.furrrlo.dui.Hooks.grantAccess;
import static io.github.furrrlo.dui.Hooks.useState;
import static org.assertj.swing.finder.WindowFinder.findFrame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GrantAccessTest extends AssertJSwingJUnit5TestCase {
    @Override
    protected void onSetUp() {
    }

    @Test
    void testGivenInsideBody() throws Exception {
        var title = "GrantAccessTest#testGivenInsideBody()";

        Application.create(app -> {
            grantAccess(MethodHandles::lookup);

            app.roots(roots -> roots.add(JDFrame.fn(frame -> {
                frame.title(() -> title);
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

            assertNotNull(windowRef.get());
        }
    }

    @Test
    void testInherited() throws Exception {
        var title = "GrantAccessTest#testLookupsReleased()";
        var result = new CompletableFuture<Collection<MethodHandles.Lookup>>();

        var cfg = ApplicationConfig.builder()
                .grantAccess(MethodHandles.lookup())
                .build();
        Application.create(cfg, app -> {
            grantAccess(MethodHandles::publicLookup);

            app.roots(roots -> roots.add(JDFrame.fn(frame -> {
                frame.title(() -> title);
                frame.contentPane(JDPanel.fn(contentPane ->
                        contentPane.children(children ->
                                children.add(JDButton.fn(btn -> {
                                    var check = useState(false);
                                    btn.name(() -> "check");
                                    btn.actionListener(e -> check.set(true));

                                    if(check.get())
                                        result.complete(StatefulDeclarativeComponent.currentLookups());
                                })))));
                frame.size(() -> new Dimension(200, 200));
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
            window.button("check").click();

            var lookups = result.get(5, TimeUnit.SECONDS);
            assertEquals(2, lookups.size());
        }
    }
}
