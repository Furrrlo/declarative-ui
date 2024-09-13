import com.github.vlsi.gradle.release.ReleaseExtension
import org.beryx.jar.JarTaskConfigurer

plugins {
    java
    id("org.beryx.jar")
}

moduleConfig {
    multiReleaseVersion.set(9)
}

val releaseParams = extensions.findByType<ReleaseExtension>()
if(releaseParams != null && releaseParams.release.get() && !JavaVersion.current().isJava9Compatible)
    throw Exception("Java 9 compatible compiler is needed for release builds")

// The compile task does not take into account the annotation processors, so it fails because of errorprone with
// error: plug-in not found: ErrorProne
// https://github.com/gradle/gradle/blob/f82eb487a99a011a2b6da8fe3c40dd1a757c6183/platforms/jvm/plugins-java-base/src/main/java/org/gradle/api/plugins/internal/JvmPluginsHelper.java#L90
// https://github.com/tbroyer/gradle-errorprone-plugin/blob/e22bbca556aca14bc10e34324675877c67ad5f0e/src/main/kotlin/net/ltgt/gradle/errorprone/ErrorPronePlugin.kt#L143-L145
tasks.register<JavaCompile>(JarTaskConfigurer.COMPILE_NON_JPMS_TASK_NAME) {
    options.annotationProcessorPath = files(tasks.compileJava.map { it.options.annotationProcessorPath ?: files() })
    options.generatedSourceOutputDirectory.set(tasks.compileJava.flatMap { it.options.generatedSourceOutputDirectory })
}

// The plugin does not set the Multi-Release manifest entry when compiling with a JPMS toolchain
tasks.jar {
    manifest.attributes("Multi-Release" to true)
}

