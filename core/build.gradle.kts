plugins {
    id("moonshine.api")
}

dependencies {
    api("io.leangen.geantyref:geantyref:1.3.11")
    implementation(project(":moonshine-internal"))
    testImplementation(project(":moonshine-standard"))
    testImplementation("net.kyori:examination-api:1.+")
    testImplementation("net.kyori:examination-string:1.+")
}
