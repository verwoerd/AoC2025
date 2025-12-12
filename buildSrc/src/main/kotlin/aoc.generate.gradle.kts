val author: String by project

tasks {
  register<DayGeneratorTask>("generateNextDay") {
    group = "aoc"
    authorBase.set(author)
    srcDir.set(layout.projectDirectory)
  }
}
