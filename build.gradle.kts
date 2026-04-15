plugins {
    kotlin("jvm") version "2.3.20"
    kotlin("plugin.serialization") version "2.3.20"
    id("com.gradleup.shadow") version "9.4.1"
    id("io.github.revxrsal.bukkitkobjects") version "0.0.5"
    id("co.uzzu.dotenv.gradle") version "4.0.0"
    java
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

bukkitKObjects {
    classes.add("gg.aquatic.crates.CratesPlugin")
}

group = "gg.aquatic.aquaticcrates"
version = "4.0.0"

tasks {
    runServer {
        minecraftVersion("1.21.11")
    }

    build {
        dependsOn(shadowJar)
    }

    processResources {
        inputs.property("version", project.version)
        filesMatching("paper-plugin.yml") {
            expand("version" to project.version)
        }
    }
}

tasks.withType(xyz.jpenilla.runtask.task.AbstractRun::class) {
    javaLauncher = javaToolchains.launcherFor {
        vendor = JvmVendorSpec.JETBRAINS
        languageVersion = JavaLanguageVersion.of(21)
    }
    jvmArgs("-XX:+AllowEnhancedClassRedefinition")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "aquatic-releases"
        url = uri("https://repo.nekroplex.com/releases")
    }
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://libraries.minecraft.net")
}

dependencies {
    val exposedVersion = "1.2.0"

    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    implementation(project(":api"))
    compileOnly("gg.aquatic:Common:26.0.17")
    compileOnly("gg.aquatic:Waves:26.0.54")
    compileOnly("gg.aquatic.execute:Execute:26.0.2")
    compileOnly("me.clip:placeholderapi:2.12.2")
    compileOnly("com.github.LoneDev6:API-ItemsAdder:3.6.2-beta-r3-b")
    compileOnly("com.ticxo.modelengine:ModelEngine:R4.0.9")
    compileOnly("io.github.toxicity188:bettermodel-bukkit-api:2.2.0")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:1.10.0")
    compileOnly("com.charleskorn.kaml:kaml:0.104.0")
    compileOnly("org.jetbrains.exposed:exposed-core:$exposedVersion")
    compileOnly("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    compileOnly("com.zaxxer:HikariCP:7.0.2")
    compileOnly("org.mariadb.jdbc:mariadb-java-client:3.5.8")
    compileOnly("org.xerial:sqlite-jdbc:3.51.3.0")

    implementation("org.reflections:reflections:0.10.2")

    // Testing
    testImplementation("io.mockk:mockk:1.14.9")
    testImplementation(kotlin("test"))
    testImplementation("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    testImplementation("gg.aquatic:Common:26.0.17")
    testImplementation("gg.aquatic:Waves:26.0.54")
    testImplementation("gg.aquatic.execute:Execute:26.0.2")
    testImplementation("com.charleskorn.kaml:kaml:0.104.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.10.0")
    testImplementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    testImplementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    testImplementation("io.github.toxicity188:bettermodel-bukkit-api:2.2.0")
    testImplementation("com.zaxxer:HikariCP:7.0.2")
    testImplementation("org.mariadb.jdbc:mariadb-java-client:3.5.8")
    testImplementation("org.xerial:sqlite-jdbc:3.51.3.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
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

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveFileName.set("AquaticCrates-${project.version}.jar")
    archiveClassifier.set("")

    dependencies {
        exclude(dependency("org.jetbrains.kotlinx:.*:.*"))
        exclude(dependency("org.jetbrains.kotlin:.*:.*"))
        exclude(dependency("org.jetbrains:annotations:.*"))
        exclude(dependency("com.intellij:annotations:.*"))
    }

    exclude("kotlin/**")
    exclude("org/intellij/**")
    exclude("org/jetbrains/annotations/**")

    relocate("kotlinx", "gg.aquatic.waves.libs.kotlinx")
    relocate("org.jetbrains.kotlin", "gg.aquatic.waves.libs.kotlin")
    relocate("kotlin", "gg.aquatic.waves.libs.kotlin")
    relocate("org.bstats", "gg.aquatic.waves.libs.bstats")

    relocate("com.zaxxer.hikari", "gg.aquatic.waves.libs.hikari")
    relocate("org.jetbrains.exposed", "gg.aquatic.waves.libs.exposed")
}
