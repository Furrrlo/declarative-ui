package io.github.furrrlo.dui.cmptw;

import com.github.caciocavallosilano.cacio.ctc.junit.CacioTest;
import io.github.furrrlo.dui.assertj.swing.AssertJSwingJUnit5TestCase;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.concurrent.TimeUnit;

import static org.assertj.swing.finder.WindowFinder.findFrame;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

@CacioTest
class CmptwTest extends AssertJSwingJUnit5TestCase {

    private FrameFixture window;

    @Override
    protected void onSetUp() {
        application(CmptwMain.class).start();
        window = findFrame(new GenericTypeMatcher<>(Frame.class) {
            protected boolean isMatching(Frame frame) {
                return "CanMyPenTabletWork".equals(frame.getTitle()) && frame.isShowing();
            }
        }).withTimeout(10, TimeUnit.SECONDS).using(robot());
    }

    @Override
    protected void onTearDown() {
        window.cleanUp();
    }

    @Test
    public void works() {
        window.comboBox().requireItemCount(2);
        window
                .panel("currentPanel")
                .tabbedPane("ApplicationsTabbedPane")
                .requireTabTitles("OneNote", "WebEx", "Firefox", "Fallback");
    }
}
