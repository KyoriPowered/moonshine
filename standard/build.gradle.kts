plugins {
    id("moonshine.api")
}

description = "Standard implementation of core functionality for moonshine"

dependencies {
    api(project(":moonshine-core"))
    implementation(project(":moonshine-internal"))
}
