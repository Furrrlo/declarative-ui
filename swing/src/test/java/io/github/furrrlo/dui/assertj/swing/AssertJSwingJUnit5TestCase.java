package io.github.furrrlo.dui.assertj.swing;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.testing.AssertJSwingTestCaseTemplate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public abstract class AssertJSwingJUnit5TestCase extends AssertJSwingTestCaseTemplate {

    @BeforeAll
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @BeforeEach
    public final void setUp() throws Exception {
        this.setUpRobot();
        this.onSetUp();
    }

    protected abstract void onSetUp() throws Exception;

    @AfterAll
    public static void tearDownOnce() {
        FailOnThreadViolationRepaintManager.uninstall();
    }

    @AfterEach
    public final void tearDown() throws Exception {
        try {
            this.onTearDown();
        } finally {
            this.cleanUp();
        }

    }

    protected void onTearDown() throws Exception {
    }
}
