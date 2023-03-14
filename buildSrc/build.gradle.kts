plugins {
    `kotlin-dsl`
}

group = "io.github.furrrlo"

dependencies {
    val voteStageReleaseVer = "1.86"
    implementation("com.github.vlsi.gradle:crlf-plugin:$voteStageReleaseVer")
    implementation("com.github.vlsi.gradle:gradle-extensions-plugin:$voteStageReleaseVer")
    implementation("com.github.vlsi.gradle:stage-vote-release-plugin:$voteStageReleaseVer")

    implementation("com.github.spotbugs.snom:spotbugs-gradle-plugin:5.0.13")
    implementation("net.ltgt.gradle:gradle-errorprone-plugin:3.0.1")
    implementation("net.ltgt.gradle:gradle-nullaway-plugin:1.5.0")
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}
