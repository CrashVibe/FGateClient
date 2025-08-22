plugins {
  kotlin("jvm") version "2.2.20-Beta2"
  kotlin("plugin.serialization") version "2.2.0"
  id("com.gradleup.shadow") version "8.3.0"
  id("xyz.jpenilla.run-paper") version "2.3.1"
}


version = properties["version"].toString()
group = properties["group"].toString()

repositories {
  mavenCentral()
  maven("https://repo.papermc.io/repository/maven-public/") {
    name = "papermc-repo"
  }
}

dependencies {
  implementation(project(":common"))
  compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
  implementation("org.bstats:bstats-bukkit:3.0.2")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
}

tasks {
  runServer {
    // Configure the Minecraft version for our task.
    // This is the only required configuration besides applying the plugin.
    // Your plugin's jar (or shadowJar if present) will be used automatically.
    minecraftVersion("1.21.4")
  }

  shadowJar {
    archiveFileName = "${rootProject.name}-bukkit-${project.version}.${archiveExtension.get()}"
    exclude("META-INF/**")
    relocate("org.java_websocket", "${project.group}.libs.websocket")
    relocate("org.bstats", "${project.group}.libs.bstats")
  }
}
