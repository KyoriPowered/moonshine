rootProject.name = "moonshine"

moonshineProject("core")
moonshineProject("standard")
moonshineProject("internal-common")
moonshineProject("internal-jre8")
moonshineProject("internal-jre9")

fun moonshineProject(path: String, name: String = "moonshine-$path"): ProjectDescriptor {
    include(path)
    val project = project(":$path")
    project.name = name
    return project
}
