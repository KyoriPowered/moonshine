plugins {
    id("moonshine.api")
}

tasks {
    compileJava {
        options.release.set(9)
        sourceCompatibility = "9"
        targetCompatibility = sourceCompatibility
    }
}

dependencies {
    api(project(":moonshine-internal-common"))
}
