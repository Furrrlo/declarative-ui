package io.github.furrrlo.dui.assertj.swing;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.exception.EdtViolationException;
import org.assertj.swing.testing.AssertJSwingTestCaseTemplate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import javax.swing.*;

public abstract class AssertJSwingJUnit5TestCase extends AssertJSwingTestCaseTemplate {

    protected static FailOnThreadViolationRepaintManager repaintManager;

    @BeforeAll
    public static void setUpOnce() {
        repaintManager = FailOnThreadViolationRepaintManager.install();
    }

    public static void ignoreEdtViolationsFor(JComponent c) {
        try {
            // Let's violate it once, as that will stop it from complaining about this specific component
            repaintManager.addInvalidComponent(c);
        } catch (EdtViolationException ex) {
            // Ignore
        }
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
