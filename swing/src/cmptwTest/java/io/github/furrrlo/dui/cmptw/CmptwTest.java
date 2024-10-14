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
        }).withTimeout(1, TimeUnit.MINUTES).using(robot());
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

    @Test
    public void fixItemDoubleSubstitutionInComponentDiffing() {
        window.button("remove_application_btn").click();
        window.button("remove_application_btn").click();
        window.button("remove_application_btn").click();

        window.button("add_application_btn").click();
        window.dialog("select_process_dialog").table().selectRows(1);
        window.dialog("select_process_dialog").button("add_btn").click();

        window.button("add_application_btn").click();
        window.dialog("select_process_dialog").table().selectRows(0);
        window.dialog("select_process_dialog").button("add_btn").click();

        window.button("add_application_btn").click();
        window.dialog("select_process_dialog").table().selectRows(2);
        window.dialog("select_process_dialog").button("add_btn").click();

        var tabbedPane = window
                        .panel("currentPanel")
                        .tabbedPane("ApplicationsTabbedPane");

        tabbedPane.requireTabTitles("Firefox", "ONENOTE", "Washost", "Fallback");
        // __JTabbedPane__components: Removing component at idx 1 of javax.swing.JTabbedPane[ApplicationsTabbedPane,35,85,634x502,layout=javax.swing.plaf.basic.BasicTabbedPaneUI$TabbedPaneScrollLayout,alignmentX=0.0,alignmentY=0.0,border=,flags=352,maximumSize=,minimumSize=,preferredSize=,haveRegistered=true,tabPlacement=TOP]
        // __JTabbedPane__components: Removing component at idx 1 of javax.swing.JTabbedPane[ApplicationsTabbedPane,35,85,634x502,layout=javax.swing.plaf.basic.BasicTabbedPaneUI$TabbedPaneScrollLayout,alignmentX=0.0,alignmentY=0.0,border=,flags=352,maximumSize=,minimumSize=,preferredSize=,haveRegistered=true,tabPlacement=TOP]
        // __JTabbedPane__components: Inserting component javax.swing.JPanel[,0,0,0x0,invalid,layout=net.miginfocom.swing.MigLayout,alignmentX=0.0,alignmentY=0.0,border=,flags=9,maximumSize=,minimumSize=,preferredSize=] at idx 0 of javax.swing.JTabbedPane[ApplicationsTabbedPane,35,85,634x502,invalid,layout=javax.swing.plaf.basic.BasicTabbedPaneUI$TabbedPaneScrollLayout,alignmentX=0.0,alignmentY=0.0,border=,flags=352,maximumSize=,minimumSize=,preferredSize=,haveRegistered=true,tabPlacement=TOP]
        // __JTabbedPane__components: Inserting component javax.swing.JPanel[,0,0,0x0,invalid,layout=net.miginfocom.swing.MigLayout,alignmentX=0.0,alignmentY=0.0,border=,flags=9,maximumSize=,minimumSize=,preferredSize=] at idx 2 of javax.swing.JTabbedPane[ApplicationsTabbedPane,35,85,634x502,invalid,layout=javax.swing.plaf.basic.BasicTabbedPaneUI$TabbedPaneScrollLayout,alignmentX=0.0,alignmentY=0.0,border=,flags=352,maximumSize=,minimumSize=,preferredSize=,haveRegistered=true,tabPlacement=TOP]
        window.button("cancel_btn").click();
        tabbedPane.requireTabTitles("OneNote", "WebEx", "Firefox", "Fallback");
    }
}
