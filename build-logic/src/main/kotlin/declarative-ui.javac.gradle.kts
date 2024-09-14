import groovy.json.JsonSlurper

plugins {
    java
}

val warningsFile = file("${rootDir}/config/javac/warnings.json")

tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(listOf("-Xmaxerrs", "2000", "-Xmaxwarns", "2000"))

    val objToString: (Any?) -> String? = { json: Any? ->
        if (json is String)
            json
        else if (json is Map<*, *> && json["value"] is String)
            json["value"] as String
        else
            null
    }

    if(warningsFile.exists()) {
        val javacWarnings = JsonSlurper().parseText(warningsFile.readText()) as Map<*, *>
        options.compilerArgs.add("-Xlint:" + listOf(
            (javacWarnings["enable"] as Collection<*>?)?.mapNotNull(objToString) ?: emptyList(),
            (javacWarnings["disable"] as Collection<*>?)?.mapNotNull(objToString)?.map { "-$it" } ?: emptyList()
        ).flatten().joinToString(","))
    }
}