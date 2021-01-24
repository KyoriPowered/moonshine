import java.util.Calendar
import nl.javadude.gradle.plugins.license.LicensePlugin
import org.checkerframework.gradle.plugin.CheckerFrameworkPlugin

plugins {
    java
    `java-library`
    checkstyle
    jacoco
    id("com.github.hierynomus.license") version "0.15.0"
    id("org.checkerframework") version "0.5.12"
}

allprojects {
    group = "com.proximyst.moonshine"
    version = "0.1.0-SNAPSHOT"
}


subprojects {
    apply {
        plugin<JavaPlugin>()
        plugin<JavaLibraryPlugin>()
        plugin<CheckstylePlugin>()
        plugin<LicensePlugin>()
        plugin<CheckerFrameworkPlugin>()
        plugin<JacocoPlugin>()
    }

    repositories {
        jcenter()
        mavenCentral()
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
                xml.isEnabled = true
                html.isEnabled = false
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
    configure<JavaPluginConvention> {
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
        toolVersion = "8.36.2"
        val configRoot = rootProject.projectDir
        configDirectory.set(configRoot)
        configProperties["basedir"] = configRoot.absolutePath
    }

    jacoco {
        reportsDirectory.set(rootProject.buildDir.resolve("reports").resolve("jacoco"))
    }

    tasks {
        compileJava {
            options.compilerArgs.add("-parameters")
        }

        compileTestJava {
            options.compilerArgs.add("-parameters")
        }
    }
}

repositories {
    jcenter() // Gradle plugins.
}
