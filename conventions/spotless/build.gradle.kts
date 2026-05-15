plugins {
  id("common")
  alias(libs.plugins.shadow)
}

dependencies {
  implementation(libs.spotless)
}

tasks {
  jar {
    archiveClassifier = "noshade"
  }
  shadowJar {
    archiveClassifier = null
  }
}

gradlePlugin {
  plugins {
    vcsUrl = "https://github.com/Strokkur424/strokk-conventions"
    website = "https://github.com/Strokkur424/strokk-conventions"
    create("strokk-conventions-spotless") {
      id = "net.strokkur.strokk-conventions-spotless"
      implementationClass = "net.strokkur.convention.SpotlessConvention"
      description = "A convention project used by many of Strokkur's projects for easier spotless application."
      displayName = "Strokk's Conventions (Spotless)"
      tags = setOf("convention", "spotless")
    }
  }
}
