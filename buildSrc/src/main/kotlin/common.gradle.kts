plugins {
  id("java-gradle-plugin")
  id("com.gradle.plugin-publish")
}

repositories {
  mavenCentral()
}

java {
  toolchain.languageVersion = JavaLanguageVersion.of(21)
}
