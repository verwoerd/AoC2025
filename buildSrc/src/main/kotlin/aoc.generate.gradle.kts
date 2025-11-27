val author: String by project

tasks {
  register("generateNextDay") {
    group = "aoc"
    actions = listOf(Action { DayGenerator.createNextDay(project.projectDir, author) })
  }
}
