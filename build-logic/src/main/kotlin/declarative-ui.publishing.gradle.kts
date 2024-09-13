import com.github.vlsi.gradle.crlf.CrLfSpec
import com.github.vlsi.gradle.crlf.LineEndings

plugins {
    java
    `maven-publish`
    id("com.github.vlsi.crlf")
    id("com.github.vlsi.gradle-extensions")
}
// TODO: can't apply it as it tries to hide the 'init' task and fails as it doesn't exist
apply(plugin = "com.github.vlsi.stage-vote-release")

description = "React-like library for Swing"

java {
    withSourcesJar()
    withJavadocJar()
}

tasks.jar {
    manifest {
        attributes["Bundle-License"] = "MIT"
        attributes["Implementation-Title"] = "declarative-ui-" + project.name
        attributes["Implementation-Version"] = project.version
        attributes["Specification-Vendor"] = "Declarative-UI"
        attributes["Specification-Version"] = project.version
        attributes["Specification-Title"] = "Declarative-UI"
        attributes["Implementation-Vendor"] = "Declarative-UI"
        attributes["Implementation-Vendor-Id"] = "io.github.furrrlo"
    }
    // Include the license
    CrLfSpec(LineEndings.LF).run {
        into("META-INF") {
            filteringCharset = "UTF-8"
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            textFrom("$projectDir/LICENSE")
        }
    }
}

tasks.withType<AbstractArchiveTask>().configureEach {
    // Ensure builds are reproducible
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
    dirMode = "775".toInt(8)
    fileMode = "664".toInt(8)
}

publishing {
    publications {
        register<MavenPublication>("maven") {
            from(components["java"])

            val publication = this
            afterEvaluate {
                publication.artifactId = "declarative-ui-" + project.name
                publication.version = project.version.toString()
            }

            pom {
                name.set("Declarative UI")
                description.set(project.provider { project.description })
                url.set("https://github.com/Furrrlo/declarative-ui")

                organization {
                    name.set("io.github.furrrlo")
                    url.set("https://github.com/Furrrlo")
                }

                issueManagement {
                    system.set("GitHub")
                    url.set("https://github.com/Furrrlo/declarative-ui/issues")
                }

                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://github.com/Furrrlo/declarative-ui/blob/master/LICENSE")
                        distribution.set("repo")
                    }
                }

                scm {
                    url.set("https://github.com/Furrrlo/declarative-ui")
                    connection.set("scm:git:git://github.com/furrrlo/declarative-ui.git")
                    developerConnection.set("scm:git:ssh://git@github.com:furrrlo/declarative-ui.git")
                }

                developers {
                    developer {
                        name.set("Francesco Ferlin")
                        url.set("https://github.com/Furrrlo")
                    }
                }
            }
        }
    }
}
