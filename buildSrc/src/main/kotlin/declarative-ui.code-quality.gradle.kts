import com.github.spotbugs.snom.Confidence
import com.github.spotbugs.snom.Effort
import com.github.spotbugs.snom.SpotBugsTask
import groovy.json.JsonSlurper
import net.ltgt.gradle.errorprone.errorprone
import net.ltgt.gradle.nullaway.nullaway
import java.util.regex.Pattern

plugins {
    id("com.github.spotbugs")
    id("net.ltgt.errorprone")
    id("net.ltgt.nullaway")
    id("pmd")
    java
}

dependencies {
    implementation("org.jetbrains:annotations:24.0.1")
    implementation("com.google.errorprone:error_prone_annotations:2.18.0")
    implementation("org.checkerframework:checker-qual:3.32.0")

    spotbugsPlugins("com.mebigfatguy.sb-contrib:sb-contrib:7.4.7")
    spotbugsPlugins("com.h3xstream.findsecbugs:findsecbugs-plugin:1.12.0")

    errorprone("com.google.errorprone:error_prone_core:2.18.0")
    errorprone("com.uber.nullaway:nullaway:0.10.10")
}

configurations.all {
    exclude(group = "com.google.code.findbugs", module = "jsr305") // Will cause problems in Java9+
    exclude(group = "javax.annotation", module = "jsr250-api") // Already in the jre
}

spotbugs {
    effort.set(Effort.MAX)
    reportLevel.set(Confidence.LOW)
    toolVersion.set("4.7.3")
    excludeFilter.set(file("${rootDir}/config/spotbugs/exclude.xml"))
}

pmd {
    ruleSets = listOf()
    toolVersion = "6.55.0"
    ruleSetConfig = resources.text.fromFile(file("${rootDir}/config/pmd/rulesSets.xml"))
}

tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(listOf("-Xmaxerrs", "2000", "-Xmaxwarns", "2000"))

    val javacWarnings = JsonSlurper().parseText(file("${rootDir}/config/javac/warnings.json").readText()) as Map<*,*>
    val objToString: (Any?) -> String? = { json: Any? ->
        if(json is String)
            json
        else if(json is Map<*, *> &&  json["value"] is String)
            json["value"] as String
        else
            null
    }
    options.compilerArgs.add("-Xlint:" + listOf(
        (javacWarnings["enable"] as Collection<*>).mapNotNull(objToString),
        (javacWarnings["disable"] as Collection<*>).mapNotNull(objToString).map { "-$it" }
    ).flatten().joinToString(","))

    options.errorprone {
        isEnabled.set(!project.ext.has("skipErrorprone"))
        // Use a provider, as options.generatedSourceOutputDirectory causes the compile task to depend on itself
        excludedPaths.set(provider {
            listOfNotNull(
                options.generatedSourceOutputDirectory.asFile.orNull
            ).flatMap { listOf(
                // I have no clue which one should actually work, the Windows one doesn't,
                // and I can't seem to debug it, so I'm just going to shove in both *nix and Windows paths
                ".*${Pattern.quote((File.separator + project.relativePath(it) + File.separator).replace(File.separator, "/"))}.*",
                ".*${Pattern.quote((File.separator + project.relativePath(it) + File.separator).replace(File.separator, "\\"))}.*"
            ) }.joinToString("|", "(?:", ")")
        })

        val patterns = JsonSlurper().parseText(file("${rootDir}/config/errorprone/patterns.json").readText()) as Map<*,*>
        // Patterns disabled in tests, 'cause it's not production code so some don't make sense
        val isTestTask = name == "compileTestJava"
        val patternsDisabledInTests = (patterns["disableInTests"] as Collection<*>).mapNotNull(objToString).toSet()
        val filterPatterns: (String) -> Boolean = { s -> !isTestTask || !patternsDisabledInTests.contains(s) }

        (patterns["enable"] as Collection<*>).mapNotNull(objToString).filter(filterPatterns).forEach { enable(it) }
        (patterns["disable"] as Collection<*>).mapNotNull(objToString).filter(filterPatterns).forEach { disable(it) }
        (patterns["error"] as Collection<*>).mapNotNull(objToString).filter(filterPatterns).forEach { error(it) }
        // Disable the checks if in test
        if(isTestTask) patternsDisabledInTests.forEach { disable(it) }

        nullaway {
            annotatedPackages.add("io.github.furrlo.dui")
            checkOptionalEmptiness.set(true)
            suggestSuppressions.set(true)
            checkContracts.set(true)
        }
    }
}

afterEvaluate {
    tasks.withType<SpotBugsTask> {
        enabled = project.ext.has("spotbugs")
        group = "SpotBugs"
        reports {
            create("xml") { required.set(true) }
            create("html") { required.set(true) }
        }
    }
}

tasks.withType<Pmd> {
    enabled = project.ext.has("pmd")
    group = "PMD"
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}
