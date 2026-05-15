rootProject.name = "strokk-conventions"

rootDir.resolve("conventions").listFiles().forEach {
  include(it.name)
  project(":${it.name}").projectDir = it
}
