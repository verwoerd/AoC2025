package day09.part1

import Coordinate
import java.io.BufferedReader
import kotlin.math.absoluteValue


/**
 * @author verwoerd
 * @since 08/12/2025
 */
fun day09Part1(input: BufferedReader): Any {
  val coordinates = input.lineSequence().map{line ->
    line.split(",").map { it.toInt() }.let { (a,b ) -> Coordinate(a,b)}
  }.sortedBy { it.y }.toList()
  return coordinates.flatMapIndexed { index, coordinate ->
    coordinates.drop(index+1).map { b-> area(coordinate, b)}
  }.max()
}


fun area(a:Coordinate, b: Coordinate): Long = ((a.x - b.x).toLong().absoluteValue+1) * ((a.y - b.y).absoluteValue+1)
