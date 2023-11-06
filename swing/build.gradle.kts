plugins {
    `java-library`
    id("declarative-ui.java-conventions")
    id("declarative-ui.code-quality")
    id("declarative-ui.testing")
    id("declarative-ui.mrjar")
    id("declarative-ui.publishing")
    id("declarative-ui.java-beans")
}

dependencies {
    api(project(":core"))
}
