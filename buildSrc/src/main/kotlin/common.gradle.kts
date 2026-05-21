plugins {
  id("java-gradle-plugin")
  id("maven-publish")
}

repositories {
  mavenCentral()
}

java {
  toolchain.languageVersion = JavaLanguageVersion.of(17)
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

tasks {
  withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-parameters")
    options.release = 17;
  }
}
