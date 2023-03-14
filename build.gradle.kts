plugins {
    id("com.github.vlsi.stage-vote-release")
}

group = "io.github.furrrlo"
version = "1.0-SNAPSHOT"

releaseParams {
    tlp.set("declarative-ui")
    organizationName.set("Furrrlo")
    componentName.set("declarative-ui")
    prefixForProperties.set("gh")
    svnDistEnabled.set(false)
    sitePreviewEnabled.set(false)
    nexus {
        prodUrl.set(project.uri("https://s01.oss.sonatype.org/service/local/"))
    }
    voteText.set {
        """
        ${it.componentName} v${it.version}-rc${it.rc} is ready for preview.
        Git SHA: ${it.gitSha}
        Staging repository: ${it.nexusRepositoryUri}
        """.trimIndent()
    }
}

subprojects {
    group = rootProject.group
    version = rootProject.version
}
