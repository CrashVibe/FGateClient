plugins {
  kotlin("jvm") version "2.2.20-Beta2"
  kotlin("plugin.serialization") version "2.2.0"
  id("com.gradleup.shadow") version "9.4.1"
}


version = properties["version"].toString()
group = properties["group"].toString()
description = properties["description"].toString()

repositories {
  mavenCentral()
  maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") {
    name = "spigot-repo"
  }
}

dependencies {
  implementation(project(":common"))
  compileOnly("org.spigotmc:spigot-api:1.21.9-R0.1-SNAPSHOT")
  implementation("org.bstats:bstats-bukkit:3.1.0")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
}

tasks {
  shadowJar {
    archiveFileName = "${rootProject.name}-bukkit-${project.version}.${archiveExtension.get()}"
    exclude("META-INF/**")
    relocate("org.java_websocket", "${project.group}.libs.websocket")
    relocate("org.bstats", "${project.group}.libs.bstats")
  }

  processResources {
    filesMatching("**/plugin.yml") {
      expand(
        "name" to rootProject.name,
        "version" to project.version,
          "description" to (project.description ?: "")
      )
    }
  }
}
