@file:Suppress("UnstableApiUsage")

plugins {
    `maven-publish`
    kotlin("jvm") version "1.9.22"
    id("dev.architectury.loom") version "1.6-SNAPSHOT"
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("me.modmuss50.mod-publish-plugin") version "0.5.+"
}

class ModData {
    val id = property("mod_id")
    val name = property("mod_name")
    val version = property("mod_version")
    val group = property("mod_group")!!
    val minecraftDependency = property("minecraft_dependency")!!
    val minSupportedVersion = property("mod_min_supported_version").toString()
    val maxSupportedVersion = property("mod_max_supported_version").toString()
}

class LoaderData {
    private val name = loom.platform.get().name.lowercase()
    val isFabric = name == "fabric"
    val isForge = name == "forge"
    val isNeoForge = name == "neoforge"

    fun getVersion() : String? {
        return if(isForge) {
            property("loader_forge")?.toString()
        } else if (isNeoForge) {
            property("loader_neoforge")?.toString()
        } else {
            null
        }
    }

    override fun toString(): String {
        return name
    }
}

class MinecraftVersionData {
    private val name = stonecutter.current.version.substringBeforeLast("-")
    val needs21 = greaterThan("1.20.4")

    fun greaterThan(other: String) : Boolean {
        return stonecutter.compare(name, other.lowercase()) > 0
    }

    fun lessThan(other: String) : Boolean {
        return stonecutter.compare(name, other.lowercase()) < 0
    }

    override fun toString(): String {
        return name
    }
}

val mod = ModData()
val loader = LoaderData()
val minecraftVersion = MinecraftVersionData()

version = "${mod.version}+$minecraftVersion"
group = mod.group

repositories {
    mavenCentral()
    maven("https://cursemaven.com")
    maven("https://maven.neoforged.net/releases/")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")

    include(implementation("org.kohsuke:github-api:${property("kohsuke_github")}")!!)
    include(implementation("com.fasterxml.jackson.core:jackson-core:${property("jackson")}")!!)
    include(implementation("com.fasterxml.jackson.core:jackson-databind:${property("jackson")}")!!)
    include(implementation("com.fasterxml.jackson.core:jackson-annotations:${property("jackson")}")!!)
}

loom {
    runConfigs.all {
        ideConfigGenerated(true)
        runDir = "../../run"
    }
}

tasks.withType<JavaCompile> {
    options.release = if (minecraftVersion.needs21) 21 else 17
}

java {
    withSourcesJar()

    val javaVersion = if (minecraftVersion.needs21) JavaVersion.VERSION_21 else JavaVersion.VERSION_17
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

val buildAndCollect = tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(tasks.remapJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${mod.version}"))
    dependsOn("build")
}

if (stonecutter.current.isActive) {
    rootProject.tasks.register("buildActive") {
        group = "project"
        dependsOn(buildAndCollect)
    }
}

if(loader.isFabric) {
    dependencies {
        modImplementation("net.fabricmc:fabric-loader:${property("fabric_loader")}")
        modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api")}")

        include(implementation("org.apache.maven:maven-artifact:${property("maven_artifact")}")!!)

        mappings("net.fabricmc:yarn:$minecraftVersion+build.${property("yarn_build")}:v2")
    }

    tasks.processResources {
        val map = mapOf(
            "version" to mod.version,
            "minecraft_dependency" to mod.minecraftDependency
        )

        inputs.properties(map)
        filesMatching("fabric.mod.json") { expand(map) }
    }
}

if (loader.isForge) {
    dependencies {
        "forge"("net.minecraftforge:forge:$minecraftVersion-${loader.getVersion()}")

        compileOnly(annotationProcessor("io.github.llamalad7:mixinextras-common:${property("mixin_extras")}")!!)
        implementation(include("io.github.llamalad7:mixinextras-forge:${property("mixin_extras")}")!!)
        if(minecraftVersion.lessThan("1.19.3")) {
            modCompileOnly("curse.maven:it-shall-not-tick-619355:${property("it_shall_not_tick")}")
        }
        if(minecraftVersion.lessThan("1.20.2")) {
            modCompileOnly("curse.maven:no-see-no-tick-833405:${property("no_see_no_tick")}")
            modCompileOnly("curse.maven:doespotatotick-825355:${property("does_potato_tick")}")
        }

        mappings("net.fabricmc:yarn:$minecraftVersion+build.${property("yarn_build")}:v2")
    }

    loom {
        forge {
            convertAccessWideners = true
            mixinConfig("${mod.id}.mixins.json")
        }
    }

    tasks.processResources {
        val map = mapOf(
            "version" to mod.version,
            "minecraft_dependency" to mod.minecraftDependency,
            "loader_version" to loader.getVersion()
        )

        inputs.properties(map)
        filesMatching("META-INF/mods.toml") { expand(map) }
    }

    if (minecraftVersion.greaterThan("1.19.4")) {
        sourceSets.forEach {
            val dir = layout.buildDirectory.dir("sourceSets/${it.name}").get().asFile
            it.output.setResourcesDir(dir)
            it.java.destinationDirectory = dir
        }
    }
}

if (loader.isNeoForge) {
    dependencies {
        "neoForge"("net.neoforged:neoforge:${loader.getVersion()}")

        mappings(loom.layered {
            mappings("net.fabricmc:yarn:$minecraftVersion+build.${property("yarn_build")}:v2")
            if(loader.isNeoForge) {
                if (minecraftVersion.lessThan("1.21")) {
                    mappings("dev.architectury:yarn-mappings-patch-neoforge:1.20.5+build.3")
                } else {
                    mappings(file("mappings/fix.tiny"))
                }
            }
        })
    }

    tasks.processResources {
        val map = mapOf(
            "version" to mod.version,
            "minecraft_dependency" to mod.minecraftDependency,
            "loader_version" to loader.getVersion()
        )

        inputs.properties(map)
        filesMatching("META-INF/neoforge.mods.toml") { expand(map) }
    }
}

publishMods {
    file = tasks.remapJar.get().archiveFile
    val tag = "$loader-${mod.version}+$minecraftVersion"
    changelog = "[Changelog](https://github.com/Bawnorton/Neruina/blob/stonecutter/CHANGELOG.md)"
    displayName = "${mod.name} ${loader.toString().replaceFirstChar { it.uppercase() }} ${mod.version} for $minecraftVersion"
    type = STABLE
    modLoaders.add(loader.toString())

    dryRun = false

    github {
        accessToken = providers.gradleProperty("GITHUB_TOKEN")
        repository = "Bawnorton/Neruina"
        commitish = "stonecutter"
        changelog = getRootProject().file("CHANGELOG.md").readLines().joinToString("\n")
        tagName = tag
    }

    modrinth {
        accessToken = providers.gradleProperty("MODRINTH_TOKEN")
        projectId = "1s5x833P"
        if(mod.minSupportedVersion == mod.maxSupportedVersion) {
            minecraftVersions.add(mod.minSupportedVersion)
        } else {
            minecraftVersionRange {
                start = mod.minSupportedVersion
                end = mod.maxSupportedVersion
            }
        }
        if(loader.isFabric) {
            requires {
                slug = "fabric-api"
            }
        }
    }

    curseforge {
        accessToken = providers.gradleProperty("CURSEFORGE_TOKEN")
        projectId = "851046"
        if(mod.minSupportedVersion == mod.maxSupportedVersion) {
            minecraftVersions.add(mod.minSupportedVersion)
        } else {
            minecraftVersionRange {
                start = mod.minSupportedVersion
                end = mod.maxSupportedVersion
            }
        }
        if(loader.isFabric) {
            requires {
                slug = "fabric-api"
            }
        }
    }
}