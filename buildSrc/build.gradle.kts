plugins {
  `kotlin-dsl`
}

repositories {
  gradlePluginPortal();
}

dependencies {
  implementation(libs.publish.plugin)
}

kotlin {
  jvmToolchain(21)
}
