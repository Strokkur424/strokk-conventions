plugins {
  id("common")
}

gradlePlugin {
  plugins {
    vcsUrl = "https://github.com/Strokkur424/strokk-conventions"
    website = "https://github.com/Strokkur424/strokk-conventions"
    create("conventions-java") {
      id = "net.strokkur.conventions-java"
      implementationClass = "net.strokkur.convention.JavaConvention"
      description = "A convention plugin used by many of Strokkur's projects."
      displayName = "Strokk's Conventions (Java)"
      tags = setOf("convention", "java", "maven-publish", "checkstyle")
    }
  }
}
