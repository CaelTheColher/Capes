import org.ajoberstar.grgit.Grgit
import org.kohsuke.github.GitHub
import org.kohsuke.github.GHReleaseBuilder
import com.matthewprenger.cursegradle.CurseProject
import com.matthewprenger.cursegradle.CurseArtifact
import com.matthewprenger.cursegradle.CurseRelation
import com.matthewprenger.cursegradle.Options
import com.modrinth.minotaur.TaskModrinthUpload
import com.modrinth.minotaur.request.VersionType

buildscript {
    dependencies {
        classpath("org.kohsuke:github-api:${project.property("github_api_version") as String}")
    }
}

plugins {
    id("maven-publish")
    id("fabric-loom")
    id("org.ajoberstar.grgit")
    id("org.jetbrains.kotlin.jvm")
    id("com.matthewprenger.cursegradle")
    id("com.modrinth.minotaur")
}

operator fun Project.get(property: String): String {
    return property(property) as String
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
}

version = project["mod_version"]
group = project["maven_group"]

val environment: Map<String, String> = System.getenv()
val releaseName = "${name.split("-").joinToString(" ") { it.capitalize() }} ${(version as String).split("+")[0]}"
val releaseType = (version as String).split("+")[0].split("-").let { if(it.size > 1) if(it[1] == "BETA" || it[1] == "ALPHA") it[1] else "ALPHA" else "RELEASE" }
val releaseFile = "${buildDir}/libs/${base.archivesBaseName}-${version}.jar"
val cfGameVersion = (version as String).split("+")[1].let{ if(!it.contains("-") && project["minecraft_version"].startsWith(it)) project["minecraft_version"] else "$it-Snapshot"}

fun getChangeLog(): String {
    return "A changelog can be found at https://github.com/lucaargolo/$name/commits/"
}

fun getBranch(): String {
    environment["GITHUB_REF"]?.let { branch ->
        return branch.substring(branch.lastIndexOf("/") + 1)
    }
    val grgit = try {
        extensions.getByName("grgit") as Grgit
    }catch (ignored: Exception) {
        return "unknown"
    }
    val branch = grgit.branch.current().name
    return branch.substring(branch.lastIndexOf("/") + 1)
}

repositories {
    jcenter()
    maven {
		name = "Fabric"
        url = uri("https://maven.fabricmc.net/")
    }
	maven {
		name = "JitPack"
		url = uri("https://jitpack.io")
	}
    maven {
        name = "Ladysnake Libs"
        url = uri("https://ladysnake.jfrog.io/artifactory/mods")
    }
    maven {
        name = "TerraformersMC"
        url = uri("https://maven.terraformersmc.com/")
    }
    mavenLocal()
}

dependencies {
    minecraft("com.mojang:minecraft:${project["minecraft_version"]}")
    mappings("net.fabricmc:yarn:${project["yarn_mappings"]}:v2")

    modImplementation("net.fabricmc:fabric-loader:${project["loader_version"]}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${project["fabric_version"]}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${project["fabric_kotlin_version"]}")

    modApi("me.sargunvohra.mcmods:autoconfig1u:${project["autoconfig_version"]}") {
        exclude(group = "net.fabricmc.fabric-api")
    }
    include("me.sargunvohra.mcmods:autoconfig1u:${project["autoconfig_version"]}")

    // Compatibility
    modImplementation("com.terraformersmc:modmenu:${project["modmenu_version"]}")
    modImplementation("dev.emi:trinkets:${project["trinkets_version"]}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    inputs.property("version", project.version)

    from(sourceSets["main"].resources.srcDirs) {
        include("fabric.mod.json")
        expand(mutableMapOf("version" to project.version))
    }

    from(sourceSets["main"].resources.srcDirs) {
        exclude("fabric.mod.json")
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(16)
}

java {
    withSourcesJar()
}

tasks.jar {
    from("LICENSE")
}

//Github publishing
task("github") {
    dependsOn(tasks.remapJar)
    group = "upload"

    onlyIf { environment.containsKey("GITHUB_TOKEN") }

    doLast {
        val github = GitHub.connectUsingOAuth(environment["GITHUB_TOKEN"])
        val repository = github.getRepository(environment["GITHUB_REPOSITORY"])

        val releaseBuilder = GHReleaseBuilder(repository, version as String)
        releaseBuilder.name(releaseName)
        releaseBuilder.body(getChangeLog())
        releaseBuilder.commitish(getBranch())

        val ghRelease = releaseBuilder.create()
        ghRelease.uploadAsset(file(releaseFile), "application/java-archive")
    }
}

//Curseforge publishing
curseforge {
    environment["CURSEFORGE_API_KEY"]?.let { apiKey = it }

    project(closureOf<CurseProject> {
        id = project["curseforge_id"]
        changelog = getChangeLog()
        releaseType = this@Build_gradle.releaseType.toLowerCase()
        addGameVersion(cfGameVersion)
        addGameVersion("Fabric")

        mainArtifact(file(releaseFile), closureOf<CurseArtifact> {
            displayName = releaseName
            relations(closureOf<CurseRelation> {
                embeddedLibrary("pal")
                optionalDependency("roughly-enough-items")
                requiredDependency("fabric-api")
                requiredDependency("fabric-language-kotlin")
            })
        })

        afterEvaluate {
            uploadTask.dependsOn("remapJar")
        }

    })

    options(closureOf<Options> {
        forgeGradleIntegration = false
    })
}

//Modrinth publishing
task<TaskModrinthUpload>("modrinth") {
    dependsOn(tasks.remapJar)
    group = "upload"

    onlyIf {
        environment.containsKey("MODRINTH_TOKEN")
    }
    token = environment["MODRINTH_TOKEN"]

    projectId = project["modrinth_id"]
    changelog = getChangeLog()

    versionNumber = version as String
    versionName = releaseName
    versionType = VersionType.valueOf(releaseType)

    uploadFile = file(releaseFile)

    addGameVersion(project["minecraft_version"])
    addLoader("fabric")
}