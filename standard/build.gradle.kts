plugins {
    id("moonshine.api")
}

dependencies {
    api(project(":moonshine-core"))
    implementation(project(":moonshine-internal-common"))
}
