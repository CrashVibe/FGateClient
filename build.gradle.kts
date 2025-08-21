plugins {
  kotlin("jvm") version "2.2.20-Beta2"
  id("com.gradleup.shadow") version "8.3.0"
}

repositories {
  maven("https://repo.papermc.io/repository/maven-public/")
  mavenCentral()
}

dependencies {
  implementation(project(":bukkit"))
}

kotlin {
  jvmToolchain(21)
}

tasks.withType<JavaCompile> {
  options.encoding = "UTF-8"
}

tasks.register<Jar>("package") {
  val outputDir = rootDir.resolve("outputs")
  outputDir.mkdirs()
  subprojects.forEach { subproject ->
    subproject.tasks.findByName("shadowJar")?.let { shadowJarTask ->
      dependsOn(shadowJarTask)
      doLast {
        val file = (shadowJarTask as AbstractArchiveTask).archiveFile.get().asFile
        file.copyTo(outputDir.resolve(file.name), true)
      }
    }
  }
}

