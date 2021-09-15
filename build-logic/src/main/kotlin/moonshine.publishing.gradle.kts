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

if (System.getenv("GITHUB_PACKAGES") != null) {
    afterEvaluate {
        if (version.toString().endsWith("-SNAPSHOT")) {
            val ref = System.getenv("GITHUB_REF")?.split('/', limit = 3)?.last()
                ?: System.getenv("GITHUB_SHA")!!.take(6)
            version = "$version+git.$ref"
        }
    }

    publishing {
        repositories {
            maven {
                name = "GithubPackages"
                url = uri("https://maven.pkg.github.com/KyoriPowered/moonshine")
                credentials {
                    username = System.getenv("GITHUB_PACKAGES_USERNAME")
                    password = System.getenv("GITHUB_PACKAGES_PASSWORD")
                }
            }
        }
    }
}
