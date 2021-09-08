plugins {
    id("net.kyori.indra.publishing")
}

indra {
    javaVersions {
        target(16)
    }

    github("KyoriPowered", "moonshine") {
        ci(true)
    }

    license {
        name("Lesser GNU General Public Licence 3.0")
        url("https://www.gnu.org/licenses/lgpl-3.0.en.html")
        spdx("LGPL-3.0-only")
    }

    configurePublications {
        pom {
            developers {
                developer {
                    id.set("Proximyst")
                    name.set("Mariell Hoversholm")
                    timezone.set("Europe/Stockholm")
                }
            }
        }
    }
}
