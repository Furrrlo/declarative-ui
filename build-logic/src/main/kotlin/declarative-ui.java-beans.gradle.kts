@file:Suppress("UnstableApiUsage")

import io.github.furrrlo.dui.gradle.JavaBeanGenTask
import org.gradle.accessors.dm.LibrariesForPluginLibs

plugins {
    java
}

val javaBeans = configurations.create("javaBeans") {
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
    }
    isVisible = false
    isCanBeConsumed = false
}

// https://github.com/gradle/gradle/issues/15383
val pluginLibs = the<LibrariesForPluginLibs>()
dependencies {
    javaBeans(pluginLibs.javabeans.classgraph)
    javaBeans(pluginLibs.javabeans.javapoet)
}

tasks.register<JavaBeanGenTask>("genFromJavaBeans") {
    group = "dui"
    classpath.from(javaBeans)
    beansToTargetPackages.set(mapOf(
        "javax.swing" to "io.github.furrrlo.dui.swing",
        "javax.accessibility" to "io.github.furrrlo.dui.swing.accessibility",
    ))
    targetDirectory.fileProvider(sourceSets.main.map { it.java.sourceDirectories.singleFile })
}