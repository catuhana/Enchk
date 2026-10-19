import net.earthcomputer.clientcommands.buildscript.CheckLanguageFilesTask
import net.earthcomputer.clientcommands.buildscript.GenerateBuildInfoTask
import net.fabricmc.loom.task.prod.ClientProductionRunTask
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion

plugins {
    // Plugin versions must be literals in the Kotlin DSL plugins block.
    id("net.fabricmc.fabric-loom") version "1.18-SNAPSHOT"
    `maven-publish`
    id("com.modrinth.minotaur") version "2.+"
    id("com.github.breadmoirai.github-release") version "2.4.1"
    checkstyle
}

val archives_base_name = property("archives_base_name") as String
val minecraft_version = property("minecraft_version") as String
val minecraft_version_dependency = property("minecraft_version_dependency") as String
val minecraft_version_list = property("minecraft_version_list") as String
val minecraft_version_list_presentable = property("minecraft_version_list_presentable") as String
val loader_version = property("loader_version") as String
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
val checkstyle_version = property("checkstyle_version") as String
val junit_version = property("junit_version") as String
val jazzer_junit_version = property("jazzer_junit_version") as String

base {
    archivesName.set(archives_base_name)
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

    runtimeOnly("me.djtheredstoner:DevAuth-fabric:$devauth_version") {
        exclude(group = "net.fabricmc", module = "fabric-loader")
    }

    add("codeGenImplementation", sourceSets.main.get().output)

    testImplementation("net.fabricmc:fabric-loader-junit:$loader_version")
    testImplementation("org.junit.jupiter:junit-jupiter:$junit_version")
    testImplementation("com.code-intelligence:jazzer-junit:$jazzer_junit_version")
}

checkstyle {
    toolVersion = checkstyle_version
    configFile = rootProject.file("checkstyle.xml")
    setIgnoreFailures(false)
}

tasks.test {
    useJUnitPlatform()
    systemProperty("clientcommands.regressionTestDir", file("regressionTests").absolutePath)
}

tasks.jar {
    from(configurations["includedLibrary"].map { if (it.isDirectory) it else zipTree(it) }) {
        include("com/seedfinding/**")
    }

    from("LICENSE")

    manifest.attributes(mapOf("Main-Class" to "net.earthcomputer.clientcommands.DoubleClickMain"))
}

val generateBuildInfo = tasks.register<GenerateBuildInfoTask>("generateBuildInfo") {
    outputFile.set(File(temporaryDir, "build_info.json"))
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("mcversion", minecraft_version_dependency)

    duplicatesStrategy = DuplicatesStrategy.WARN

    from(sourceSets.main.get().resources.srcDirs) {
        include("fabric.mod.json")
        expand(mapOf("version" to project.version, "mcversion" to minecraft_version_dependency))
    }

    from(sourceSets.main.get().resources.srcDirs) {
        exclude("fabric.mod.json")
    }

    from(generateBuildInfo)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.register<ClientProductionRunTask>("prodClient") {
    runDir.set(file("run"))

    jvmArgs.add("-Ddevauth.enabled=true")

    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
}

tasks.register<Jar>("sourcesJar") {
    dependsOn(tasks.named("classes"))
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

tasks.register<JavaExec>("codeGen") {
    mainClass.set("net.earthcomputer.clientcommands.codegen.CodeGenerator")
    classpath = sourceSets["codeGen"].runtimeClasspath
    args(file("src/main/java").absolutePath)
    enableAssertions = true
}

val checkLanguageFiles = tasks.register<CheckLanguageFilesTask>("checkLanguageFiles") {
    inputDir.set(file("src/main/resources/assets/clientcommands/lang"))
}
tasks.named("check") {
    dependsOn(checkLanguageFiles)
}

// read the changelog from the changelog.txt file, make sure the changelog is recent to avoid accidentally using the previous version's changelog.
val changelogFile = file("changelog.txt")
val changelogText = if (changelogFile.exists() && System.currentTimeMillis() - changelogFile.lastModified() <= 1000 * 60 * 60) {
    changelogFile.readLines(Charsets.UTF_8).joinToString("\n")
} else {
    ""
}

modrinth {
    token.set(if (project.hasProperty("modrinthKey")) project.property("modrinthKey") as String else System.getenv("MODRINTH_TOKEN"))
    projectId.set("client-commands")
    uploadFile.set(tasks.jar)
    gameVersions.set(minecraft_version_list.split(Regex("\\s*,\\s*")))
    loaders.set(listOf("fabric", "quilt"))
    changelog.set(changelogText)
    dependencies {
        required.project("fabric-api")
        embedded.project("betterconfig")
        embedded.project("simplewaypoints")
    }
}
tasks.named("modrinth") {
    onlyIf { changelogText.isNotBlank() }
}

githubRelease {
    token(if (project.hasProperty("githubKey")) project.property("githubKey") as String else (System.getenv("GITHUB_TOKEN") ?: ""))
    owner("Earthcomputer")
    repo("clientcommands")
    targetCommitish("fabric")
    releaseName("Version $version for Minecraft $minecraft_version_list_presentable")
    body(changelogText)
    releaseAssets(tasks.jar.get().outputs)
}
tasks.named("githubRelease") {
    onlyIf { changelogText.isNotBlank() }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifact(tasks.jar)
            artifact(tasks.named("sourcesJar"))
        }
    }

    repositories {
        // mavenLocal()
    }
}
