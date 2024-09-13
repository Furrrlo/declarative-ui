@file:Suppress("UnstableApiUsage")

plugins {
    `kotlin-dsl`
}

group = "io.github.furrrlo.dui.gradle"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(pluginLibs.vlsi.crlf)
    implementation(pluginLibs.vlsi.gradleExtensions)
    implementation(pluginLibs.vlsi.stageVoteRelease)
    implementation(pluginLibs.badassJar)

    implementation("com.github.spotbugs.snom:spotbugs-gradle-plugin:5.0.13")
    implementation("net.ltgt.gradle:gradle-errorprone-plugin:3.0.1")
    implementation("net.ltgt.gradle:gradle-nullaway-plugin:1.5.0")

    compileOnly("io.github.classgraph:classgraph:4.8.164")
    compileOnly("com.squareup:javapoet:1.13.0")
}
