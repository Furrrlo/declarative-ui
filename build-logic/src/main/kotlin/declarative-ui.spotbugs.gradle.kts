@file:Suppress("UnstableApiUsage")

import com.github.spotbugs.snom.Confidence
import com.github.spotbugs.snom.Effort
import com.github.spotbugs.snom.SpotBugsTask
import org.gradle.accessors.dm.LibrariesForCodeQualityLibs
import java.util.*

plugins {
    id("com.github.spotbugs")
}

val spotBugsFile = file("${rootDir}/config/spotbugs/exclude.xml")
val spotBugsEnabled = project.ext.has("spotbugs")

// https://github.com/gradle/gradle/issues/15383
val codeQualityLibs = the<LibrariesForCodeQualityLibs>()
dependencies {
    spotbugsPlugins(codeQualityLibs.spotbugsPlugins.sb.contrib)
    spotbugsPlugins(codeQualityLibs.spotbugsPlugins.findsecbugs)
}

configurations.all {
    // Do not touch the spotbugs config, otherwise it will flat out refuse to work
    if(!this.name.toLowerCase(Locale.ROOT).contains("spotbugs")) {
        exclude(group = "com.google.code.findbugs", module = "jsr305") // Will cause problems in Java9+
        exclude(group = "javax.annotation", module = "jsr250-api") // Already in the jre
    }
}

spotbugs {
    effort.set(Effort.MAX)
    reportLevel.set(Confidence.LOW)
    toolVersion.set(codeQualityLibs.versions.spotbugs)
    if(spotBugsFile.exists()) excludeFilter.set(spotBugsFile)
}

afterEvaluate {
    tasks.withType<SpotBugsTask> {
        enabled = spotBugsEnabled
        group = "SpotBugs"
        reports {
            create("xml") { required.set(true) }
            create("html") { required.set(true) }
        }
    }
}