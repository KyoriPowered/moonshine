plugins {
    id("moonshine.api")
}

dependencies {
    api("io.leangen.geantyref:geantyref:1.3.11")
    implementation(project(":moonshine-internal-common"))
    implementation(project(":moonshine-internal-jre8"))
    implementation(project(":moonshine-internal-jre9"))
    testImplementation(project(":moonshine-standard"))
    testImplementation("net.kyori:examination-api:1.+")
    testImplementation("net.kyori:examination-string:1.+")
}
