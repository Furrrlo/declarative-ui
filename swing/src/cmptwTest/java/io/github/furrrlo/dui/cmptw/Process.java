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
    Path ROOT_ICON_PATH = Path.of("src/cmptwTest/resources/io/github/furrrlo/dui/cmptw").toAbsolutePath().normalize();

    int pid();

    String name();

    Path iconPath();

    static Collection<Process> enumerateProcesses() {
        return List.of(
                new ProcessImpl(1, "ONENOTE.exe", ROOT_ICON_PATH.resolve("onenote.png")),
                new ProcessImpl(2, "washost.exe", ROOT_ICON_PATH.resolve("webex.png")),
                new ProcessImpl(3, "firefox.exe", ROOT_ICON_PATH.resolve("firefox.png")));
    }

    static List<BufferedImage> extractProcessIcons(Path processFile) {
        if(!Files.exists(processFile))
            return getFallbackIcons();

        try {
            return List.of(ImageIO.read(processFile.toFile()));
        } catch (IOException e) {
            throw new UncheckedIOException(processFile.toFile().toString(), e);
        }
    }

    static List<BufferedImage> getFallbackIcons() {
        try {
            return List.of(ImageIO.read(ROOT_ICON_PATH.resolve("fallback.png").toFile()));
        } catch (IOException e) {
            throw new UncheckedIOException(
                    ROOT_ICON_PATH.resolve("fallback.png").toFile().toString(), e);
        }
    }

    record ProcessImpl(int pid, String name, Path iconPath) implements Process {
    }
}
