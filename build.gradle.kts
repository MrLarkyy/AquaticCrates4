plugins {
    kotlin("jvm") version "2.2.21"
    id("io.github.revxrsal.bukkitkobjects") version "0.0.5"
}

bukkitKObjects {
    classes.add("gg.aquatic.crates.CratesPlugin")
}

group = "gg.aquatic.klocale"
version = "4.0.0"

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    implementation(project(":api"))
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}