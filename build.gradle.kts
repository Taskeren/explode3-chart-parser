@file:Suppress("SpellCheckingInspection")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
plugins {
    kotlin("jvm") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    `maven-publish`
}

group = "cn.taskeren"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.13.4")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.4")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "cn.taskeren.explode3.parser.cli.MainKt"
        )
    }
}

publishing {
    repositories {
        maven {
            name = "GithubPackages"
            url = uri("https://maven.pkg.github.com/Taskeren/explode3-chart-parser")
            credentials {
                username = project.findProperty("gpr.username")?.toString() ?: System.getenv("GPR_USERNAME")
                password = project.findProperty("gpr.password")?.toString() ?: System.getenv("GPR_PASSWORD")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}