plugins {
  id("aoc.problem")
}
project.application.mainClass.set("MainKt")

dependencies {
  implementation("org.ojalgo:ojalgo:56.1.1")
}
repositories {
  mavenCentral()
}
