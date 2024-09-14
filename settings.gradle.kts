@file:Suppress("UnstableApiUsage")

rootProject.name = "declarative-ui"

includeBuild("build-logic")

include(":core")
include(":swing")

dependencyResolutionManagement {
    versionCatalogs {
        create("pluginLibs") {
            from(files("./gradle/plugins.versions.toml"))
        }

        create("codeQualityLibs") {
            from(files("./gradle/code-quality.versions.toml"))
        }

        create("cmptwLibs") {
            from(files("./gradle/cmptw.versions.toml"))
        }
    }
}
