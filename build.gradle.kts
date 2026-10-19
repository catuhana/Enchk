import net.fabricmc.loom.task.prod.ClientProductionRunTask
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion

plugins {
    id("net.fabricmc.fabric-loom") version "1.19.1"
}

val minecraftVersion = property("minecraft_version") as String
val loaderVersion = property("loader_version") as String
val fabricVersion = property("fabric_version") as String

val modVersion = property("mod_version") as String

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
}

loom {
    accessWidenerPath.set(file("src/main/resources/clientcommands.aw"))
}

loom.runs.configureEach {
    jvmArguments.add("-enableassertions")
    systemProperties.put("fabric.loom.suppressNarratorError", "true")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")

    implementation("net.fabricmc:fabric-loader:$loaderVersion")
    implementation("net.fabricmc.fabric-api:fabric-api:$fabricVersion")

    include(implementation(libs.betterconfig.fabric.get())!!)

    listOf(
        libs.mc.seed,

        variantOf(libs.latticg) { classifier("rt") }
    ).forEach {
        add("includedLibrary", it)
    }

    listOf(
        libs.latticg,
        libs.jspecify,

        sourceSets.main.get().output
    ).forEach {
        add("codeGenImplementation", it)
    }

    compileOnly(libs.mcdev.annotations)
}

tasks {
    jar {
        from(configurations["includedLibrary"].map { if (it.isDirectory) it else zipTree(it) }) {
            include("com/seedfinding/**")
        }

        from("LICENSE")
    }

    processResources {
        duplicatesStrategy = DuplicatesStrategy.WARN

        val properties = mapOf(
            "mod_version" to modVersion,
            "minecraft_version" to minecraftVersion,
            "loader_version" to loaderVersion,
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
