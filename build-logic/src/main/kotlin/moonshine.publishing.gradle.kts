plugins {
    id("net.kyori.indra.publishing")
}

if (System.getenv("CI").toBoolean()) {
    val signingKey = System.getenv("SIGNING_KEY")
    val signingPassword = System.getenv("SIGNING_PASSWORD")
    signing.useInMemoryPgpKeys(signingKey, signingPassword)
}

indra {
    javaVersions {
        target(17)
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
        if (System.getenv("CI").toBoolean()) {
            signing.sign(this)
        }

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
