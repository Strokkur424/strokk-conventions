plugins {
  id("java-gradle-plugin")
  id("maven-publish")
}

repositories {
  mavenCentral()
}

java {
  toolchain.languageVersion = JavaLanguageVersion.of(21)
}

publishing {
  repositories {
    maven("https://eldonexus.de/repository/maven-releases/") {
      name = "Eldonexus"
      credentials {
        username = System.getenv("NEXUS_USERNAME") ?: ""
        password = System.getenv("NEXUS_PASSWORD") ?: ""
      }
    }
  }
}

java {
  toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks {
  withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-parameters")
    options.release = 25;
  }
}
