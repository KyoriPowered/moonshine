configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_9
    targetCompatibility = sourceCompatibility
    disableAutoTargetJvm()
}

dependencies {
    api(project(":internal-common"))
}
