package io.github.furrrlo.dui.cmptw;

import com.github.weisj.darklaf.LafManager;
import io.github.furrrlo.dui.Application;
import io.github.furrrlo.dui.ApplicationConfig;
import io.github.furrrlo.dui.swing.JDFrame;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import org.fife.ui.rsyntaxtextarea.folding.CurlyFoldParser;
import org.fife.ui.rsyntaxtextarea.folding.FoldParserManager;
import org.oxbow.swingbits.dialog.task.IContentDesign;
import org.oxbow.swingbits.dialog.task.TaskDialogs;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.logging.Level;

public class CmptwMain {

    private static final List<Hook> INITIAL_HOOKS = List.of(new Hook(
            new Hook.Device("HID\\VID_28BD&PID_0905&REV_0000&MI_00&Col03", "Pentablet", "HID Keyboard Device"),
            List.of(new Hook.ApplicationHook(
                            new Hook.Application(
                                    "ONENOTE.EXE",
                                    "OneNote",
                                    Process.ROOT_ICON_PATH.resolve("onenote.png")),
                            List.of(
                                    new Hook.HookScript(
                                            "Key 1",
                                            new Hook.KeyStroke(66, 256, 0),
                                            """
                                                  #NoEnv  ; Recommended for performance and compatibility with future AutoHotkey releases.
                                                  #Warn  ; Enable warnings to assist with detecting common errors.
                                                  SendMode Input  ; Recommended for new scripts due to its superior speed and reliability.
                                                  SetWorkingDir %A_ScriptDir%  ; Ensures a consistent starting directory.
                                                  SetStoreCapsLockMode, Off ; Messes with OneNote shortcuts
                                                                                                      
                                                  SetKeyDelay 10
                                                  Send {Esc}{Alt}
                                                  Sleep 100
                                                  Send {d}{c}
                                                  """),
                                    new Hook.HookScript("Key 2", new Hook.KeyStroke(69, 256, 0), ""),
                                    new Hook.HookScript("Key 3", new Hook.KeyStroke(73, 256, 0), ""),
                                    new Hook.HookScript("Key 4", new Hook.KeyStroke(32, 256, 0), ""),
                                    new Hook.HookScript("Key 5", new Hook.KeyStroke(90, 324, 0), ""),
                                    new Hook.HookScript("Key 6", new Hook.KeyStroke(83, 260, 0), ""),
                                    new Hook.HookScript("Key 7", new Hook.KeyStroke(107, 260, 0), ""),
                                    new Hook.HookScript("Key 8", new Hook.KeyStroke(109, 260, 0), ""))),
                    new Hook.ApplicationHook(
                            new Hook.Application(
                                    "washost.exe",
                                    "WebEx",
                                    Process.ROOT_ICON_PATH.resolve("webex.png")),
                            List.of()),
                    new Hook.ApplicationHook(
                            new Hook.Application(
                                    "firefox.exe",
                                    "Firefox",
                                    Process.ROOT_ICON_PATH.resolve("firefox.png")),
                            List.of())
            ),
            Hook.FallbackBehavior.DELETE_AND_PLAY_SOUND));

    public static void main(String[] args) {
        try {
            doMain();
        } catch (Exception ex) {
            LoggerFactory.getLogger(CmptwMain.class).error("Failed to start", ex);
            TaskDialogs.showException(new Exception("Failed to start", ex));
            System.exit(-1);
        }
    }

    private static void doMain() throws Exception {
        java.util.logging.Logger.getLogger("").setLevel(Level.FINEST);

        IconFontSwing.register(FontAwesome.getIconFont());
        LafManager.registerDefaultsAdjustmentTask((currentTheme, properties) -> {
            UIManager.getDefaults().put(IContentDesign.COLOR_MESSAGE_BACKGROUND, properties.get("controlBackground"));
            UIManager.getDefaults().put(IContentDesign.COLOR_INSTRUCTION_FOREGROUND, properties.get("textForegroundDefault"));
        });
        // Native crash on caciotta as darklaf it tries to grab the window hwnd with JNI
        if(!Boolean.getBoolean("dui.useCaciotta")) {
            LafManager.install();
            LafManager.installTheme(LafManager.getPreferredThemeStyle());
        }
        FoldParserManager.get().addFoldParserMapping("text/ahk", new CurlyFoldParser());

        var cfg = ApplicationConfig.builder()
                .grantAccess(MethodHandles.lookup())
                .build();
        Application.create(cfg, app -> app.roots(roots -> roots.add(JDFrame.fn(frame -> {
            frame.title(() -> "CanMyPenTabletWork");
            frame.defaultCloseOperation(() -> WindowConstants.EXIT_ON_CLOSE);
            frame.contentPane(JDHooksPane.fn(INITIAL_HOOKS));
            frame.minimumSize(() -> new Dimension(700, 700));
            frame.size(() -> new Dimension(700, 700));
            frame.locationRelativeTo(() -> null);
            frame.visible(() -> true);
        }))));

        LafManager.enabledPreferenceChangeReporting(true);
//        LafManager.addThemePreferenceChangeListener(tray);

//        SwingUtilities.invokeLater(tray::showGui);
    }
}
