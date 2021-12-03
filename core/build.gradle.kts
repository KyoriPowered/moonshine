plugins {
    id("moonshine.api")
}

description = "Core functionality of moonshine"

dependencies {
    api(libs.geantyref)
    implementation(project(":moonshine-internal"))
    testImplementation(project(":moonshine-standard"))
    testImplementation(libs.examination.api)
    testImplementation(libs.examination.string)
}
