import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Files
import java.text.DateFormat
import java.util.Date
import java.util.Locale

/**
 * @author verwoerd
 * @since 08-11-20
 */
@CacheableTask
abstract class DayGeneratorTask : DefaultTask() {
  @get:Input
  abstract val authorBase: Property<String>

  @get:InputDirectory
  @get:PathSensitive(PathSensitivity.RELATIVE)
  abstract val srcDir: DirectoryProperty

  private val moduleRegex = Regex("day(\\d\\d)")

  fun findLastDay(base: File): Int =
    base.list()!!.mapNotNull { moduleRegex.matchEntire(it) }.maxOfOrNull { it.groupValues[1].toInt() } ?: 0

  @TaskAction
  fun generate() {
    val dir = srcDir.get().asFile
    val author = authorBase.get()
    createNextDay(dir, author)
  }

  fun createNextDay(base: File, author: String) {
    val next = findLastDay(base).inc().toString().padStart(2, '0')
    println("the next day to generate is $next")
    val path = FileSystems.getDefault().getPath(base.absolutePath)
    val template = path.resolve("template")
    val module = Files.createDirectory(path.resolve("day$next"))
    println("created directory $module")
    Files.copy(template.resolve("build.gradle.kts"), module.resolve("build.gradle.kts"))
    val kotlinMain = module.resolve("src").resolve("main").resolve("kotlin")
    val resourcesMain = module.resolve("src").resolve("main").resolve("resources")
    val kotlinTest = module.resolve("src").resolve("test").resolve("kotlin")
    val resourcesTest = module.resolve("src").resolve("test").resolve("resources")
    Files.createDirectories(kotlinMain)
    Files.createDirectories(kotlinTest)
    Files.createDirectories(resourcesMain)
    Files.createDirectories(resourcesTest)
    Files.copy(template.resolve("src/main/kotlin/main.kt"), kotlinMain.resolve("main.kt"))
    // copy test input
    template.resolve("src/test/resources").toFile().copyRecursively(resourcesTest.toFile())
    // copy solution files in their own day package to make stacktraces easier
    template.resolve("src/main/kotlin/day").toFile()
      .copyRecursively(Files.createDirectories(kotlinMain.resolve("day$next")).toFile())
    // copy test files
    template.resolve("src/test/kotlin/day").toFile()
      .copyRecursively(Files.createDirectories(kotlinTest.resolve("day$next")).toFile())
    println("Succesfully created the file skeleton")
    // fix package name, created date and author
    val date = DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRANCE).format(Date()) // note: dd/mm/yy
    println("Using date $date")
    println("Using author $author")
    listOf(
      "src/main/kotlin/day$next/part1/solution.kt",
      "src/main/kotlin/day$next/part2/solution.kt",
      "src/main/kotlin/main.kt",
      "src/test/kotlin/day$next/part1/Part1Test.kt",
      "src/test/kotlin/day$next/part2/Part2Test.kt"
    ).map {
      module.resolve(it)
    }.forEach { filename ->
      val lines = Files.readAllLines(filename).map {
        when {
          it.startsWith("package") || it.startsWith("import day") -> it.replace("day", "day$next")
          it.startsWith(" * @author") -> " * @author $author"
          it.startsWith(" * @since") -> " * @since $date"
          it.contains("day") -> it.replace("day", "day$next")
          else -> it
        }
      }
      Files.write(filename, lines)
      println("Updated file $filename")
    }
    // Update settings.gradle.kts
    val settings = path.resolve("settings.gradle.kts")
    val lines = Files.readAllLines(settings)
    lines[lines.size - 2] = lines[lines.size - 2] + ","
    lines[lines.size - 1] = """  "day$next""""
    lines.add("       )")
    Files.write(settings, lines)
  }
}
