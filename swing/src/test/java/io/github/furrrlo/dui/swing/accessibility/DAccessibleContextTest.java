package io.github.furrrlo.dui.swing.accessibility;

import io.github.furrrlo.dui.Application;
import io.github.furrrlo.dui.ApplicationConfig;
import io.github.furrrlo.dui.assertj.swing.AssertJSwingJUnit5TestCase;
import io.github.furrrlo.dui.swing.JDFrame;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.swing.finder.WindowFinder.findFrame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DAccessibleContextTest extends AssertJSwingJUnit5TestCase {

    @Override
    protected void onSetUp() {
    }

    @Test
    public void testInnerAccessibleContext() throws Exception {
        var title = "DAccessibleContextTest#testInnerAccessibleContext()";

        var cfg = ApplicationConfig.builder()
                .grantAccess(MethodHandles.lookup())
                .build();
        Application.create(cfg, app -> app.roots(roots -> roots.add(JDFrame.fn(frame -> {
            frame.title(() -> "DAccessibleContextTest#testInnerAccessibleContext()");
            frame.accessibleContext((accessibleCtx) -> {
                accessibleCtx.accessibleName(() -> title + " name");
                accessibleCtx.accessibleDescription(() -> title + " desc");
            });
            frame.visible(() -> true);
        }))));

        var windowRef = new AtomicReference<FrameFixture>();
        try(AutoCloseable windowCloseable = () -> windowRef.get().cleanUp()) {
            @SuppressWarnings("unused") var unused = windowCloseable;
            windowRef.set(
                    findFrame(new GenericTypeMatcher<>(Frame.class) {
                        @Override
                        protected boolean isMatching(Frame frame) {
                            return title.equals(frame.getTitle()) && frame.isShowing();
                        }
                    }).withTimeout(1, TimeUnit.MINUTES).using(robot())
            );

            var window = windowRef.get();
            var accessibleCtx = window.target().getAccessibleContext();
            assertNotNull(window.target().getAccessibleContext());
            assertEquals(title + " name", accessibleCtx.getAccessibleName());
            assertEquals(title + " desc", accessibleCtx.getAccessibleDescription());
        }
    }
}