import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "2.2.0"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.17"
}

group = "com.mgvpri.fabo.langzeit"
version = "2.0"

repositories {
    mavenCentral()
}

dependencies {
    paperweight.paperDevBundle("1.21.7-R0.1-SNAPSHOT")

    implementation("net.axay:kspigot:1.21.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
    implementation(files("lib/fabo-rank-plugin-2.0.jar"))
}

tasks {
    build {
        dependsOn(reobfJar)
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }
}