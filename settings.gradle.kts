rootProject.name = "conventions"

rootDir.resolve("conventions").listFiles().forEach {
  val name = "conventions-${it.name}"
  include(name)
  project(":$name").projectDir = it
}
