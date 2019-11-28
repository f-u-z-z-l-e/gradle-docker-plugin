plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.3.50"
    id("java-gradle-plugin")
    id("com.gradle.plugin-publish") version "0.10.1"
    id("ch.fuzzle.gradle.semver") version "0.3.2"
    jacoco
    id("pl.droidsonroids.jacoco.testkit") version "1.0.5"
}

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("semVerPlugin") {
            id = "ch.fuzzle.gradle.docker-plugin"
            displayName = "gradle docker plugin"
            description = "This plugin adds tasks to build a docker image for the given project."
            implementationClass = "DockerPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/f-u-z-z-l-e/gradle-docker-plugin"
    vcsUrl = "https://github.com/f-u-z-z-l-e/gradle-docker-plugin"
    tags = listOf("gradle-plugin", "docker", "docker-image-builder", "kotlin")
}