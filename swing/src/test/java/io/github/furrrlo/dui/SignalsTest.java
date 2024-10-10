package io.github.furrrlo.dui;

import io.github.furrrlo.dui.assertj.swing.AssertJSwingJUnit5TestCase;
import io.github.furrrlo.dui.swing.JDFrame;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.furrrlo.dui.Hooks.*;
import static org.assertj.swing.finder.WindowFinder.findFrame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SignalsTest extends AssertJSwingJUnit5TestCase {

    @Override
    protected void onSetUp() {
    }

    @Test
    void testMultipleCallsInSameFun() throws Exception {
        var title = "PropsTest#testUpdates()";
        var numOfSignals = new AtomicInteger();

        Application.create(app -> {
            grantAccess(MethodHandles::lookup);

            app.roots(roots -> roots.add(JDFrame.fn(frame -> {
                var state = useState("example name");
                var memo = useMemo(() -> state.get() + " yohooho");
                useSideEffect(() -> numOfSignals.set(((InternalMemo) memo).getSignalDependenciesSize()));

                frame.title(() -> title);
                frame.name(() -> {
                    // Call the same memo multiple times
                    return memo.get() + " " + memo.get() + " " + memo.map(t -> t + "!!!");
                });
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
            assertEquals(
                    "example name yohooho example name yohooho example name yohooho!!!",
                    window.target().getName());
            assertEquals(1, numOfSignals.get(),
                    "Multiple calls rom the same function to the same memo should only result in 1 signal");
        }
    }
}
