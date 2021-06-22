import nl.javadude.gradle.plugins.license.LicensePlugin
import org.checkerframework.gradle.plugin.CheckerFrameworkPlugin
import java.util.*

plugins {
    java
    `java-library`
    `maven-publish`
    checkstyle
    jacoco
    idea
    id("com.github.hierynomus.license") version "0.16.1"
    id("org.checkerframework") version "0.5.22"
}

allprojects {
    group = "net.kyori.moonshine"
    version = "2.0.0-SNAPSHOT"
}

subprojects {
    apply {
        plugin<JavaPlugin>()
        plugin<JavaLibraryPlugin>()
        plugin<MavenPublishPlugin>()
        plugin<CheckstylePlugin>()
        plugin<LicensePlugin>()
        plugin<CheckerFrameworkPlugin>()
        plugin<JacocoPlugin>()
        plugin<IdeaPlugin>()
    }

    dependencies {
        api("com.google.guava:guava:30.1-jre")
        api("io.leangen.geantyref:geantyref:1.3.4")

        testImplementation("org.junit.jupiter:junit-jupiter:5.+")
        testImplementation("org.mockito:mockito-core:3.+")
        testImplementation("org.mockito:mockito-junit-jupiter:3.+")
        testImplementation("org.assertj:assertj-core:3.+")

        testImplementation("org.apache.commons:commons-lang3:3.11")
    }

    tasks {
        val jacocoTestReport by getting(JacocoReport::class)
        test {
            useJUnitPlatform()
            dependsOn(checkstyleMain, checkstyleTest)
            dependsOn(licenseMain, licenseTest)
            finalizedBy(jacocoTestReport)
        }

        jacocoTestReport {
            dependsOn(test)
            reports {
                xml.required.set(true)
                html.required.set(false)
            }
        }

        javadoc {
            val opt = options as StandardJavadocDocletOptions
            opt.addStringOption("Xdoclint:none", "-quiet")

            opt.encoding("UTF-8")
            opt.charSet("UTF-8")
            opt.source("8")
            doFirst {
                opt.links(
                    "https://docs.oracle.com/javase/8/docs/api/"
                )
            }
        }
    }
}

// These are some options that either won't apply to rootProject, or
// will be nice to have to disable potential warnings and errors.
allprojects {
    repositories {
        mavenCentral()
    }

    extensions.configure(JavaPluginExtension::class) {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = sourceCompatibility
        disableAutoTargetJvm()
    }

    license {
        header = rootProject.file("LICENCE-HEADER")
        ext["year"] = Calendar.getInstance().get(Calendar.YEAR)
        include("**/*.java")

        mapping("java", "DOUBLESLASH_STYLE")
    }

    checkstyle {
        toolVersion = "8.43"
        val configRoot = rootProject.projectDir.resolve(".checkstyle")
        configDirectory.set(configRoot)
        configProperties["basedir"] = configRoot.absolutePath
    }

    jacoco {
        reportsDirectory.set(rootProject.buildDir.resolve("reports").resolve("jacoco"))
    }

    java {
        withSourcesJar()
        withJavadocJar()
    }

    tasks {
        compileJava {
            options.compilerArgs.add("-parameters")
        }

        compileTestJava {
            options.compilerArgs.add("-parameters")
        }
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
            }
        }

        repositories {
            maven {
                name = "proxi-nexus"
                val repo = if (project.version.toString().endsWith("-SNAPSHOT")) {
                    "snapshots"
                } else {
                    "releases"
                }
                url = uri("https://nexus.mardroemmar.dev/repository/maven-$repo/")
                credentials {
                    val proxiUser: String? by project
                    val proxiPassword: String? by project
                    username = proxiUser
                    password = proxiPassword
                }
            }
        }
    }
}
