package day07.part2

import Coordinate
import toCoordinateMap
import java.io.BufferedReader
import kotlin.collections.firstNotNullOf

/**
 * @author verwoerd
 * @since 07/12/2025
 */
fun day07Part2(input: BufferedReader): Any {
  val maze = input.toCoordinateMap()
  return maze.countPaths(maze.filter{it.value == 'S'}.firstNotNullOf { it.key })
}

val cache = mutableMapOf<Coordinate, Long>()
fun Map<Coordinate, Char>.countPaths(point: Coordinate):Long = cache.getOrPut(point) {
  val token = get(point)
  when (token) {
      '^' -> countPaths(point.plusX(-1)) + countPaths(point.plusX(1))
      null -> 1
      else -> countPaths(point.plusY(1))
  }
}


