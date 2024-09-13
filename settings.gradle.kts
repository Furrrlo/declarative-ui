@file:Suppress("UnstableApiUsage")

rootProject.name = "declarative-ui"

includeBuild("build-logic")

include(":core")
include(":swing")

dependencyResolutionManagement {
    versionCatalogs {
        create("cmptwLibs") {
            from(files("./gradle/cmptw.versions.toml"))
        }
    }
}