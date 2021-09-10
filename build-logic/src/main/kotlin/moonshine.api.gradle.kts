import com.adarshr.gradle.testlogger.theme.ThemeType
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("moonshine.publishing")
    id("net.kyori.indra")
//    id("net.kyori.indra.checkstyle")
    id("net.kyori.indra.license-header")
    id("com.adarshr.test-logger")
    java
    `java-library`
    jacoco
}

//indra {
//    checkstyle {
//        toolVersion = "9.0"
//    }
//}

testlogger {
    theme = ThemeType.MOCHA_PARALLEL
    showPassed = true
}

configurations {
    testCompileClasspath {
        exclude(group = "junit")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    val libs = (project as ExtensionAware).extensions.getByName("libs") as LibrariesForLibs
    api(libs.checkerframework)

    testImplementation(libs.bundles.testing.api)
    testRuntimeOnly(libs.bundles.testing.runtime)
}

tasks {
    withType<JavaCompile> {
        options.compilerArgs.add("-parameters")
    }

    javadoc {
        val opt = options as StandardJavadocDocletOptions
        opt.addStringOption("Xdoclint:none", "-quiet")

        opt.encoding("UTF-8")
        opt.charSet("UTF-8")
        doFirst {
            opt.links(
                "https://docs.oracle.com/en/java/javase/16/docs/api/",
            )
        }
    }
}
