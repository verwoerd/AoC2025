package day04.part1

import adjacentCircularCoordinates
import toCoordinateMap
import java.io.BufferedReader

/**
 * @author verwoerd
 * @since 03/12/2025
 */
fun day04Part1(input: BufferedReader): Any {
  val maze = input.toCoordinateMap()
  return maze.keys.filter { maze[it]!! == '@' }
    .count { adjacentCircularCoordinates(it).count { c -> maze.getOrElse(c) { '.' } == '@' } < 4 }
}
