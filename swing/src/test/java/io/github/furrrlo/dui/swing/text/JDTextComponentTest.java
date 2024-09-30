package io.github.furrrlo.dui.swing.text;

import io.github.furrrlo.dui.Application;
import io.github.furrrlo.dui.ApplicationConfig;
import io.github.furrrlo.dui.Hooks;
import io.github.furrrlo.dui.ThrowingConsumer;
import io.github.furrrlo.dui.assertj.swing.AssertJSwingJUnit5TestCase;
import io.github.furrrlo.dui.swing.JDFrame;
import io.github.furrrlo.dui.swing.JDPanel;
import io.github.furrrlo.dui.swing.JDTextField;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.fixture.FrameFixture;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.furrrlo.dui.Hooks.grantAccess;
import static io.github.furrrlo.dui.Hooks.useLaunchedEffect;
import static org.assertj.swing.finder.WindowFinder.findFrame;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JDTextComponentTest extends AssertJSwingJUnit5TestCase {

    @Override
    protected void onSetUp() {
    }

    @Test
    void testTextChangeListener() throws Exception {
        var title = "JDTextComponentTest#testTextChangeListener()";
        doTest(
                title,
                txt -> {
                    // Use ignoreEdtViolations for all operations cause technically they should be allowed
                    // from any thread, but the CheckThreadViolationRepaintManager complains anyway
                    ignoreEdtViolationsFor(txt);

                    Thread.sleep(200);
                    txt.setText("ciao");
                    Thread.sleep(200);
                    ((AbstractDocument) txt.getDocument()).replace(0, 4, "Ciao mondo!", null);
                    Thread.sleep(200);
                    txt.getDocument().remove(0, 11);
                },
                stuffReceived -> {
                    Thread.sleep(200 * 3);

                    record ToMatch(@Nullable String prevTxt, @Nullable String newTxt) {
                    }
                    assertEquals(
                            List.of(
                                    new ToMatch("initial text", "ciao"),
                                    new ToMatch("ciao", "Ciao mondo!"),
                                    new ToMatch("Ciao mondo!", "")),
                            stuffReceived.stream()
                                    .map(e -> new ToMatch(e.getPrevText(), e.getNewText()))
                                    .toList());
                }
        );
    }

    @Test
    void testTextChangeListenerDocumentChanged() throws Exception {
        var title = "JDTextComponentTest#testTextChangeListenerDocumentChanged()";
        doTest(
                title,
                txt -> {
                    // Use ignoreEdtViolations for all operations cause technically they should be allowed
                    // from any thread, but the CheckThreadViolationRepaintManager complains anyway
                    ignoreEdtViolationsFor(txt);

                    Thread.sleep(200);
                    var doc = new PlainDocument();
                    doc.insertString(0, "ciao", null);
                    txt.setDocument(doc);
                    Thread.sleep(200);
                    ((AbstractDocument) txt.getDocument()).replace(0, 4, "Ciao mondo!", null);
                    Thread.sleep(200);
                    txt.getDocument().remove(0, 11);
                },
                stuffReceived -> {
                    Thread.sleep(200 * 3);

                    record ToMatch(@Nullable String prevTxt, @Nullable String newTxt) {
                    }
                    assertEquals(
                            List.of(
                                    new ToMatch("initial text", "ciao"),
                                    new ToMatch("ciao", "Ciao mondo!"),
                                    new ToMatch("Ciao mondo!", "")),
                            stuffReceived.stream()
                                    .map(e -> new ToMatch(e.getPrevText(), e.getNewText()))
                                    .toList());
                }
        );
    }

    private void doTest(String title,
                        ThrowingConsumer<JTextField> makeChanges,
                        ThrowingConsumer<List<TextChangeEvent>> checkChanges) throws Exception {
        var initLatch = new CountDownLatch(1);
        var evtListenerRef = new AtomicReference<TextChangeListener>();

        var cfg = ApplicationConfig.builder()
                .grantAccess(MethodHandles.lookup())
                .build();
        Application.create(cfg, app -> {
            grantAccess(MethodHandles::publicLookup);

            app.roots(roots -> roots.add(JDFrame.fn(frame -> {
                frame.title(() -> title);
                frame.contentPane(JDPanel.fn(contentPane ->
                        contentPane.children(children ->
                                children.add(JDTextField.fn(txt -> {
                                    var ref = Hooks.<JTextField>useThrowingRef("Missing txt field to check");

                                    useLaunchedEffect(() -> {
                                        if(initLatch.await(90, TimeUnit.SECONDS))
                                            makeChanges.accept(ref.curr());
                                    });

                                    txt.ref(ref);
                                    txt.name(() -> "check");
                                    txt.text(() -> "initial text");
                                    txt.textChangeListener(evt -> {
                                        var l = evtListenerRef.get();
                                        if(l != null)
                                            l.textChanged(evt);
                                    });
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

            final List<TextChangeEvent> stuffReceived = new CopyOnWriteArrayList<>();
            evtListenerRef.set(stuffReceived::add);
            initLatch.countDown();

            Thread.sleep(2000);
            checkChanges.accept(stuffReceived);
        }
    }
}