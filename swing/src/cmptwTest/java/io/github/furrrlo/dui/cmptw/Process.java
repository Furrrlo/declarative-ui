package io.github.furrrlo.dui.cmptw;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

interface Process {

    Collection<String> PROCESS_EXTENSIONS = Collections.singleton("exe");
    Collection<String> ICON_EXTENSIONS = List.of("exe", "ico");
    Path ROOT_ICON_PATH = Path.of("src\\cmptwTest\\resources\\io\\github\\furrrlo\\dui\\cmptw");

    int pid();

    String name();

    Path iconPath();

    static Collection<Process> enumerateProcesses() {
        return List.of(
                new ProcessImpl(1, "OneNote", ROOT_ICON_PATH.resolve("onenote.png")),
                new ProcessImpl(2, "WebEx", ROOT_ICON_PATH.resolve("webex.png")),
                new ProcessImpl(3, "Firefox", ROOT_ICON_PATH.resolve("firefox.png")));
    }

    static List<BufferedImage> extractProcessIcons(Path processFile) {
        if(!Files.exists(processFile))
            return getFallbackIcons();

        try {
            return List.of(ImageIO.read(processFile.toFile()));
        } catch (IOException e) {
            throw new UncheckedIOException(processFile.toAbsolutePath().toString(), e);
        }
    }

    static List<BufferedImage> getFallbackIcons() {
        try {
            return List.of(ImageIO.read(ROOT_ICON_PATH.resolve("fallback.png").toFile()));
        } catch (IOException e) {
            throw new UncheckedIOException(
                    ROOT_ICON_PATH.resolve("fallback.png").toAbsolutePath().toString(), e);
        }
    }

    record ProcessImpl(int pid, String name, Path iconPath) implements Process {
    }
}
