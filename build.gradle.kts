plugins {
    kotlin("jvm") version "2.3.0"
    id("com.gradleup.shadow") version "9.2.2"
    id("io.github.revxrsal.bukkitkobjects") version "0.0.5"
    id("xyz.jpenilla.gremlin-gradle") version "0.0.7"
    id("co.uzzu.dotenv.gradle") version "4.0.0"
    java
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
    maven {
        name = "aquatic-releases"
        url = uri("https://repo.nekroplex.com/releases")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    implementation(project(":api"))
    implementation("gg.aquatic:KRegistry:25.0.1")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    implementation("org.reflections:reflections:0.10.2")
    implementation("net.kyori:adventure-text-minimessage:4.25.0")
    implementation("net.kyori:adventure-text-serializer-gson:4.25.0")
    implementation("net.kyori:adventure-text-serializer-plain:4.25.0")

    testImplementation(kotlin("test"))
    testImplementation("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}

subprojects {
    apply(plugin = "kotlin")

    version = rootProject.version
    kotlin {
        jvmToolchain(21)
    }
}