plugins {
    `kotlin-dsl`
}

group = "io.github.furrrlo.dui.gradle"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    compileOnly("io.github.classgraph:classgraph:4.8.164")
    compileOnly("com.squareup:javapoet:1.13.0")
}
