plugins {
    id("moonshine.publishing")
    `java-platform`
}

description = "Bill of materials for moonshine"

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
