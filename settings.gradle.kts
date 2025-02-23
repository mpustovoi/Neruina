import dev.kikugie.stonecutter.StonecutterSettings

pluginManagement {
	repositories {
		maven("https://maven.fabricmc.net/")
		maven("https://maven.architectury.dev")
		maven("https://maven.minecraftforge.net/")
		maven("https://maven.neoforged.net/releases/")
		maven("https://maven.kikugie.dev/releases/")
		mavenCentral()
		gradlePluginPortal()
	}
}

plugins {
	id("dev.kikugie.stonecutter") version "0.4"
}

fun getProperty(key: String): String? {
	return settings.extra[key] as? String
}

fun getVersions(key: String): Set<String> {
	return getProperty(key)!!.split(',').map { it.trim() }.toSet()
}

val versions = mapOf(
	"forge" to getVersions("forge_versions"),
	"fabric" to getVersions("fabric_versions"),
	"neoforge" to getVersions("neoforge_versions")
)

val sharedVersions = versions.map { entry ->
	val loader = entry.key
	entry.value.map { "$it-$loader" }
}.flatten().toSet()

extensions.configure<StonecutterSettings> {
	kotlinController = true
	centralScript = "build.gradle.kts"

	shared {
		versions(sharedVersions)
	}

	create(rootProject)
}

rootProject.name = "Neruina"