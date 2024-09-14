import org.gradle.accessors.dm.LibrariesForCodeQualityLibs

plugins {
    id("pmd")
}

val pmdRuleSetsFile = file("${rootDir}/gradle/pmd/rulesSets.xml")
val pmdEnabled = project.ext.has("pmd")

// https://github.com/gradle/gradle/issues/15383
val codeQualityLibs = the<LibrariesForCodeQualityLibs>()
pmd {
    ruleSets = listOf()
    toolVersion = codeQualityLibs.versions.pmd.get()
    if(pmdRuleSetsFile.exists()) ruleSetConfig = resources.text.fromFile(pmdRuleSetsFile)
}

tasks.withType<Pmd> {
    enabled = pmdEnabled
    group = "PMD"
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    isConsoleOutput = true
}