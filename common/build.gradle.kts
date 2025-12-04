plugins {
  kotlin("jvm")
  kotlin("plugin.serialization") version "2.2.0"
  id("com.github.gmazzo.buildconfig") version "5.3.5"
}

version = properties["version"].toString()
group = properties["group"].toString()

repositories {
  mavenCentral()
}

dependencies {
  implementation("de.exlll:configlib-yaml:4.6.1")
  implementation("org.java-websocket:Java-WebSocket:1.6.0")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
}

buildConfig {
  packageName(group as String)
  buildConfigField("String", "apiVersion", "\"${properties["apiVersion"].toString()}\"")
}
