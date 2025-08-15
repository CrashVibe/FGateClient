plugins {
  kotlin("jvm") version "2.2.20-Beta2"
  kotlin("plugin.serialization") version "2.2.0"
  id("com.gradleup.shadow") version "8.3.0"
  id("xyz.jpenilla.run-paper") version "2.3.1"
}


group = "org.crashvibe"
version = "0.0.1"

repositories {
  mavenCentral()
  maven("https://repo.papermc.io/repository/maven-public/") {
    name = "papermc-repo"
  }
}

dependencies {
  compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
  implementation("org.java-websocket:Java-WebSocket:1.6.0")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("de.exlll:configlib-yaml:4.6.1")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
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
    archiveFileName = "${rootProject.name}-Modern-${project.version}.${archiveExtension.get()}"
    exclude("META-INF/**")
    relocate("org.java_websocket", "${project.group}.libs.websocket")
    relocate("org.bstats", "${project.group}.libs.bstats")
  }

  build {
    dependsOn(shadowJar)
  }
}

val targetJavaVersion = 21
kotlin {
  jvmToolchain(targetJavaVersion)
}

tasks.build {
  dependsOn("shadowJar")
}

tasks.processResources {
  val props = mapOf("version" to version)
  inputs.properties(props)
  filteringCharset = "UTF-8"
  filesMatching("paper-plugin.yml") {
    expand(props)
  }
}
