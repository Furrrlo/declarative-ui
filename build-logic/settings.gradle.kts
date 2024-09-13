@file:Suppress("UnstableApiUsage")

rootProject.name = "declarative-ui-build-logic"

dependencyResolutionManagement {
    versionCatalogs {
        create("pluginLibs") {
            from(files("../gradle/plugins.versions.toml"))
        }
    }
}
