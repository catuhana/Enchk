import net.fabricmc.loom.task.prod.ClientProductionRunTask
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion

plugins {
    id("net.fabricmc.fabric-loom") version "1.18-SNAPSHOT"
}

val minecraft_version = property("minecraft_version") as String
val loader_version = property("loader_version") as String

val mod_version = property("mod_version") as String

val fabric_api_version = property("fabric_api_version") as String
val clientarguments_version = property("clientarguments_version") as String
val betterconfig_version = property("betterconfig_version") as String
val simplewaypoints_version = property("simplewaypoints_version") as String
val seedfinding_feature_version = property("seedfinding_feature_version") as String
val seedfinding_biome_version = property("seedfinding_biome_version") as String
val seedfinding_core_version = property("seedfinding_core_version") as String
val seedfinding_seed_version = property("seedfinding_seed_version") as String
val latticg_version = property("latticg_version") as String
val devauth_version = property("devauth_version") as String

sourceSets {
    create("codeGen")
}

val includedLibrary = configurations.create("includedLibrary")
configurations.named("implementation") {
    extendsFrom(includedLibrary)
}

repositories {
    maven("https://maven.seedfinding.com")
    maven("https://maven.latticg.com")
    maven("https://maven.xpple.dev/maven2")
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
    maven("https://maven.parchmentmc.org")
}

loom {
    accessWidenerPath.set(file("src/main/resources/clientcommands.aw"))
}

loom.runs.configureEach {
    jvmArguments.add("-enableassertions")
    systemProperties.put("fabric.loom.suppressNarratorError", "true")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft_version")
    implementation("net.fabricmc:fabric-loader:$loader_version")
    implementation("net.fabricmc.fabric-api:fabric-api:$fabric_api_version")

    include(implementation("dev.xpple:clientarguments:$clientarguments_version")!!)
    include(implementation("dev.xpple:betterconfig-fabric:$betterconfig_version")!!)
    include(implementation("dev.xpple:simplewaypoints:$simplewaypoints_version")!!)

    add("includedLibrary", "com.seedfinding:mc_feature:$seedfinding_feature_version")
    add("includedLibrary", "com.seedfinding:mc_biome:$seedfinding_biome_version")
    add("includedLibrary", "com.seedfinding:mc_core:$seedfinding_core_version")
    add("includedLibrary", "com.seedfinding:mc_seed:$seedfinding_seed_version")

    add("includedLibrary", "com.seedfinding:latticg:$latticg_version:rt")
    add("codeGenImplementation", "com.seedfinding:latticg:$latticg_version")

    add("codeGenImplementation", "org.jspecify:jspecify:1.0.0")

    compileOnly("com.demonwav.mcdev:annotations:2.0.0")

    add("codeGenImplementation", sourceSets.main.get().output)
}

tasks {
    jar {
        from(configurations["includedLibrary"].map { if (it.isDirectory) it else zipTree(it) }) {
            include("com/seedfinding/**")
        }

        from("LICENSE")

        manifest.attributes(mapOf("Main-Class" to "net.earthcomputer.clientcommands.DoubleClickMain"))
    }

    processResources {
        duplicatesStrategy = DuplicatesStrategy.WARN

        val properties = mapOf(
            "mod_version" to mod_version,
            "minecraft_version" to minecraft_version,
            "loader_version" to loader_version,
        )

        inputs.properties(properties)

        filesMatching("fabric.mod.json") {
            expand(properties)
        }
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    @Suppress("UnstableApiUsage")
    register<ClientProductionRunTask>("prodClientTask") {
        description = ""

        runDir = file("run")
    }

    register<JavaExec>("codeGenTask") {
        description = ""

        enableAssertions = true

        classpath = sourceSets["codeGen"].runtimeClasspath
        mainClass = "net.earthcomputer.clientcommands.codegen.CodeGenerator"

        args(file("src/main/java").absolutePath)
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}
