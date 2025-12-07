package day07.part1

import Coordinate
import priorityQueueOf
import toCoordinateMap
import yRange
import java.io.BufferedReader

/**
 * @author verwoerd
 * @since 07/12/2025
 */
fun day07Part1(input: BufferedReader): Any {
  return calculateEndpoints(input.toCoordinateMap())
}

fun calculateEndpoints(maze: Map<Coordinate, Char>): Int {
  val start = maze.filter{it.value == 'S'}.firstNotNullOf { it.key }
  val comperator: Comparator<Coordinate> = Comparator.comparingInt { it.y }
  val queue = priorityQueueOf(comperator)
  queue.add(start)
  var splitCount = 0
  val seen = mutableSetOf<Coordinate>()
  val yRange = maze.yRange().let { (a,b) -> a..b }
  while(queue.isNotEmpty()) {
    val current = queue.poll()
    if(seen.contains(current)) continue
    seen.add(current)
    if(current.y !in yRange) continue
    val tile = maze.getOrElse(current) { '.' }
    when (tile) {
      '^' -> listOf(current.plusX(1), current.plusX(-1)).toCollection(queue).also { splitCount++ }
      else -> queue.add(current.plusY(1))
    }
  }
  return splitCount
}
