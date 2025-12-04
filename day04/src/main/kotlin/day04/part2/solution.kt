package day04.part2

import Coordinate
import adjacentCircularCoordinates
import toCoordinateMap
import java.io.BufferedReader

/**
 * @author verwoerd
 * @since 03/12/2025
 */
fun day04Part2(input: BufferedReader): Any {
  val maze = input.toCoordinateMap().toMutableMap()
  var changed: List<Coordinate>
  var count = 0
  do {
    changed = maze.keys.filter { maze[it]!! == '@' }
      .filter { adjacentCircularCoordinates(it).count { c -> maze.getOrElse(c) { '.' } == '@' } < 4 }
    count += changed.size
    changed.forEach { maze[it] = '.' }
  } while (changed.isNotEmpty())
  return count
}
