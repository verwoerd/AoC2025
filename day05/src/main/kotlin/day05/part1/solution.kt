package day05.part1

import java.io.BufferedReader

/**
 * @author verwoerd
 * @since 05/12/2025
 */
fun day05Part1(input: BufferedReader): Any {
  val lines = input.readLines()
  val ranges = lines.takeWhile { it.isNotEmpty() }.map { line ->
    val (a,b) = line.split("-").map { it.toLong() }
    a..b
  }
  return lines.drop(ranges.size+1).map { line -> line.toLong() }
    .count{i -> ranges.any {i in it }}
}
