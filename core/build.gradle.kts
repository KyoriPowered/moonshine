plugins {
    id("moonshine.api")
}

dependencies {
    api(libs.geantyref)
    implementation(project(":moonshine-internal"))
    testImplementation(project(":moonshine-standard"))
    testImplementation(libs.examination.api)
    testImplementation(libs.examination.string)
}
