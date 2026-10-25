import net.fabricmc.loom.task.prod.ClientProductionRunTask

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.fabric.loom)
}

val modVersion = property("mod_version") as String
val javaVersion = libs.versions.java.get()

kotlin {
    jvmToolchain(javaVersion.toInt())
}

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
    accessWidenerPath = file("src/main/resources/enchk.aw")

    splitEnvironmentSourceSets()

    mods {
        register("enchk") {
            sourceSet("client")
            sourceSet("main")
        }
    }
}

dependencies {
    minecraft(libs.minecraft)

    implementation(libs.fabric.loader)
    implementation(libs.fabric.api)

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

        sourceSets["client"].output,
        sourceSets["main"].output
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
            "minecraft_version" to libs.versions.minecraft.get(),
            "java_version" to javaVersion,
            "loader_version" to libs.versions.fabric.loader.get(),
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
        mainClass = "io.github.catuhana.enchk.codegen.CodeGenerator"

        args(file("src/main/java").absolutePath)
    }
}
