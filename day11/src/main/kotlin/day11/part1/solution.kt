package day11.part1

import java.io.BufferedReader

/**
 * @author verwoerd
 * @since 11/12/2025
 */
fun day11Part1(input: BufferedReader): Any {
  val network = input.readNetwork()
  val start = "you"
  val cache = mutableMapOf<String, Long>()
  fun computePath(from: String): Long {
    return if (from == "out") 1 else cache.getOrPut(from) {
      network.getValue(from).fold(0L) { acc, next -> acc + computePath(next) }
    }
  }
  return computePath(start)
}

fun BufferedReader.readNetwork() = readLines().associate { line ->
  line.split(": ").let { (key, values) ->
    key to values.split(" ")
  }
}
