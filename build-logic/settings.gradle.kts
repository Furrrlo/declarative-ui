@file:Suppress("UnstableApiUsage")

rootProject.name = "declarative-ui-build-logic"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }

        create("pluginLibs") {
            from(files("../gradle/plugins.versions.toml"))
        }

        create("codeQualityLibs") {
            from(files("../gradle/code-quality.versions.toml"))
        }
    }
}
