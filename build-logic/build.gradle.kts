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

    implementation(codeQualityLibs.plgns.spotbugs)
    implementation(codeQualityLibs.plgns.errorprone)
    implementation(codeQualityLibs.plgns.nullaway)

    compileOnly("io.github.classgraph:classgraph:4.8.164")
    compileOnly("com.squareup:javapoet:1.13.0")

    // https://github.com/gradle/gradle/issues/15383
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(files(pluginLibs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(files(codeQualityLibs.javaClass.superclass.protectionDomain.codeSource.location))
}
