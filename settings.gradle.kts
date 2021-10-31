pluginManagement {
    operator fun Settings.get(property: String): String {
        return org.gradle.api.internal.plugins.DslObject(this).asDynamicObject.getProperty(property) as String
    }

    repositories {
        gradlePluginPortal()
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        mavenLocal()
    }

    plugins {
        id("org.jetbrains.kotlin.jvm") version settings["kotlin_version"]
        id("fabric-loom") version settings["loom_version"]
        id ("org.ajoberstar.grgit") version settings["grgit_version"]
        id ("com.matthewprenger.cursegradle") version settings["cursegradle_version"]
        id ("com.modrinth.minotaur") version settings["modrinth_version"]
    }
}