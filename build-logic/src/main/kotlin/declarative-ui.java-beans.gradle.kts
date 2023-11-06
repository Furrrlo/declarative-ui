import io.github.furrrlo.dui.gradle.JavaBeanGenTask

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

dependencies {
    javaBeans("io.github.classgraph:classgraph:4.8.164")
    javaBeans("com.squareup:javapoet:1.13.0")
}

tasks.register<JavaBeanGenTask>("genFromJavaBeans") {
    group = "dui"
    classpath.from(javaBeans)
    beansToTargetPackages.set(mapOf("javax.swing" to "io.github.furrrlo.dui.swing"))
    targetDirectory.fileProvider(sourceSets.main.map { it.java.sourceDirectories.singleFile })
}