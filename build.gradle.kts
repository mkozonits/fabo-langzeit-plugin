import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.serialization") version "2.0.20"
    id("io.papermc.paperweight.userdev") version "1.7.7"
}

group = "com.mgvpri.fabo.langzeit"
version = "2.0"

repositories {
    mavenCentral()
}

dependencies {
    paperweight.paperDevBundle("1.21.3-R0.1-SNAPSHOT")

    implementation("net.axay:kspigot:1.21.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation(files("lib/fabo-rank-plugin-2.0-dev.jar"))
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