@file:Suppress("UnstableApiUsage")

plugins {
    `java-library`
    id("declarative-ui.java-conventions")
    id("declarative-ui.code-quality")
    id("declarative-ui.testing")
    id("declarative-ui.mrjar")
    id("declarative-ui.publishing")
    id("declarative-ui.java-beans")
}

val cmptwTest by sourceSets.creating
configurations[cmptwTest.implementationConfigurationName].extendsFrom(configurations.testImplementation.get())
configurations[cmptwTest.runtimeOnlyConfigurationName].extendsFrom(configurations.testRuntimeOnly.get())

dependencies {
    api(project(":core"))

    testImplementation(project(":swing"))
    testImplementation(libs.assertjswing)
    testImplementation(libs.caciotta)

    val cmptwTestImplementation by configurations
    cmptwTestImplementation(sourceSets.test.map { it.output })
    cmptwTestImplementation(cmptwLibs.bundles.darklaf)
    cmptwTestImplementation(cmptwLibs.taskdialogs)
    cmptwTestImplementation(cmptwLibs.swingx)
    cmptwTestImplementation(cmptwLibs.miglayout)
    cmptwTestImplementation(cmptwLibs.bundles.jiconfont)
    cmptwTestImplementation(cmptwLibs.rsyntaxtextarea)
    cmptwTestImplementation(cmptwLibs.autocomplete)
}

listOf(tasks.compileTestJava, tasks.named<JavaCompile>("compileCmptwTestJava")).forEach { it.configure {
    sourceCompatibility = JavaVersion.VERSION_17.toString()
    targetCompatibility = JavaVersion.VERSION_17.toString()
}}

val useCaciotta = true
tasks.test {
    useJUnitPlatform()

    testClassesDirs += cmptwTest.output.classesDirs
    classpath += configurations[cmptwTest.runtimeClasspathConfigurationName] + cmptwTest.output

    if(useCaciotta) {
        systemProperty("dui.useCaciotta", useCaciotta)
        systemProperty("awt.toolkit", "com.github.caciocavallosilano.cacio.ctc.CTCToolkit")
        systemProperty("java.awt.graphicsenv", "com.github.caciocavallosilano.cacio.ctc.CTCGraphicsEnvironment")

        jvmArgs(listOf(
//            "-XX:+EnableDynamicAgentLoading",
            "--add-exports=java.desktop/java.awt=ALL-UNNAMED",
            "--add-exports=java.desktop/java.awt.peer=ALL-UNNAMED",
            "--add-exports=java.desktop/sun.awt.image=ALL-UNNAMED",
            "--add-exports=java.desktop/sun.java2d=ALL-UNNAMED",
            "--add-exports=java.desktop/java.awt.dnd.peer=ALL-UNNAMED",
            "--add-exports=java.desktop/sun.awt=ALL-UNNAMED",
            "--add-exports=java.desktop/sun.awt.event=ALL-UNNAMED",
            "--add-exports=java.desktop/sun.awt.datatransfer=ALL-UNNAMED",
            "--add-exports=java.base/sun.security.action=ALL-UNNAMED",
            "--add-opens=java.base/java.util=ALL-UNNAMED",
            "--add-opens=java.desktop/java.awt=ALL-UNNAMED",
            "--add-opens=java.desktop/sun.java2d=ALL-UNNAMED",
            "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED"
        ))
    }
}
