plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(libs.gradle.plugin.indra)
    implementation(libs.gradle.plugin.indra.publishing)
    implementation(libs.gradle.plugin.testlog)

    implementation(files(libs.javaClass.protectionDomain.codeSource.location))
}
