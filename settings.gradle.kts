enableFeaturePreview("VERSION_CATALOGS")

pluginManagement {
    includeBuild("build-logic")
}

rootProject.name = "moonshine"

moonshineProject("core")
moonshineProject("standard")
moonshineProject("internal")
moonshineProject("bom")

fun moonshineProject(path: String, name: String = "moonshine-$path"): ProjectDescriptor {
    include(path)
    val project = project(":$path")
    project.name = name
    return project
}
