import java.io.BufferedReader

fun Sequence<String>.extractLongSequence() = map { it.splitSpaceSeperatedLongs() }

fun String.splitSpaceSeperatedLongs() = split(" ").map { it.toLong() }

fun <T> BufferedReader.toSpecializedCoordinateMap(converter: (Char) -> T): Map<Coordinate, T> =
  lineSequence().flatMapIndexed { y, line ->
    line.mapIndexed { x, char -> Coordinate(x, y) to converter(char) }
  }.toMap()

fun BufferedReader.toCoordinateMap() = toSpecializedCoordinateMap { char -> char }

fun BufferedReader.rawMaze() = lineSequence().toList()
