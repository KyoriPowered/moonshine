plugins {
    id("moonshine.publishing")
    `java-platform`
}

indra {
    configurePublications {
        from(components["javaPlatform"])
    }
}

dependencies {
    constraints {
        sequenceOf(
            "core",
            "standard",
        ).forEach {
            api(project(":moonshine-$it"))
        }
    }
}
