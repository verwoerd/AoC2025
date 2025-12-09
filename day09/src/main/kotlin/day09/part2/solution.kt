package day09.part2

import Coordinate
import day09.part1.area
import java.io.BufferedReader
import java.util.TreeMap

/**
 * @author verwoerd
 * @since 08/12/2025
 */
fun day09Part2(input: BufferedReader): Any {
  val coordinates = input.lineSequence().map { line ->
    line.split(",").map { it.toInt() }.let { (a, b) -> Coordinate(a, b) }
  }.toList()
  val polygon = Polygon(coordinates)
//  val cache = mutableMapOf<Coordinate, Boolean>()
  val combinations = coordinates.flatMapIndexed { index, c ->
    coordinates.drop(index+1).filter { it.x != c.x && it.y != c.y }
      .map { b -> c to b }
  }.sortedByDescending {(a,b) -> area(a,b) }


  return combinations.first { (a,b) ->
    val minX = minOf(a.x, b.x)
    val maxX = maxOf(a.x, b.x)
    val minY = minOf(a.y, b.y)
    val maxY = maxOf(a.y, b.y)
// Original slow solution, check the border to be fully in the polygon or border
//    (minY..maxY).all { y ->
//      cache.getOrPut(Coordinate(minX,y)) {
//        polygon.isInPolygon(Coordinate(minX, y))
//      } && cache.getOrPut(Coordinate(maxX,y)) {
//        polygon.isInPolygon(Coordinate(maxX, y))
//      }
//    } &&
//    (minX..maxX).all { x ->
//      cache.getOrPut(Coordinate(x,minY)) {
//        polygon.isInPolygon(Coordinate(x, minY))
//      } && cache.getOrPut(Coordinate(x,maxY)) {
//        polygon.isInPolygon(Coordinate(x, maxY))
//      }
//    }
    polygon.containsRectangle(minX, maxX, minY, maxY)
  }.let { (a,b) -> area(a,b) }
}



class Polygon(points: List<Coordinate>) {
  private val intervals = TreeMap<Int, List<Int>>()
  private val horizontalEdges = TreeMap<Int, MutableList<IntRange>>()
  private val verticalEdges = mutableSetOf<Pair<Int, IntRange>>()

  init {
    val base = (points + points.first())
    // determine vertical lines
    val vertical =
      base.zipWithNext().filter { (a, b) -> a.x == b.x }.map { (a, b) -> a.x to minOf(a.y, b.y)..maxOf(a.y, b.y) }
        .groupBy { it.first }.mapValues { v -> v.value.map { it.second } }
    // determine unique y borders
    val yBorders = vertical.values.flatten().flatMap { listOf(it.first, it.last) }.distinct().sorted()

    yBorders.zipWithNext().associateTo(intervals) { (start, end) ->
      start to vertical.filter { (key, value) ->
        value.any { it.first <= start && it.last >= end }
      }.map { it.key }.sorted()
    }

    //fill edge detection
    base.zipWithNext() { a, b ->
      when {
        a.y == b.y -> horizontalEdges.computeIfAbsent(a.y) { mutableListOf() }.add(minOf(a.x, b.x)..maxOf(a.x, b.x))
        a.x == b.x -> verticalEdges += a.x to minOf(a.y, b.y)..maxOf(a.y, b.y)
      }
    }
  }

  fun containsRectangle(minX: Int, maxX: Int, minY: Int, maxY: Int): Boolean {
    val subMap = horizontalEdges.subMap(minY + 1, maxY)
    if (subMap.isNotEmpty()) {
      if(subMap.values.any { e -> e.any {  it.first < maxX && it.last > minX  } }) return false
    }
    val testY = (minY + maxY) / 2
    val row = intervals.floorEntry(testY)?.value ?: return false

    // Find the wall strictly to the left or at minX
    val index = row.binarySearch(minX)
    val wallIndex = if (index >= 0) index else -(index + 1) - 1
    if (wallIndex < 0) return false
    if (wallIndex % 2 != 0) return false


    // If the next wall is before maxX, then the polygon ends inside our rectangle.
    return wallIndex + 1 < row.size && row[wallIndex + 1] >= maxX
  }


  private fun onBoundary(coordinate: Coordinate) =
    horizontalEdges.getValue(coordinate.y).any{coordinate.x in it}
        || verticalEdges.any { (x, yRange) -> coordinate.x == x && coordinate.y in yRange }

  fun isInPolygon(coordinate: Coordinate): Boolean {
    if (onBoundary(coordinate)) return true

    val entry = intervals.floorEntry(coordinate.y) ?: return false
    val snijpunten = entry.value
    val count = snijpunten.binarySearch(coordinate.x).let { index ->
      when {
        index >= 0 -> index + 1
        else -> -(index + 1)
      }
    }
    return count % 2 == 1
  }
}
