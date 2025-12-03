package day03.part2

import java.io.BufferedReader

/**
 * @author verwoerd
 * @since 02/12/2025
 */
fun day03Part2(input: BufferedReader): Any {
  return input.readLines().sumOf { joltage(it) }.toLong()
}

val cache = mutableMapOf<Pair<String, Int>, Long>()
fun joltage(line: String, size: Int = 12): Long {
  var best = 0L
  for (i in 0..line.length - size) {
    best = when (size) {
        1 -> "${line[i]}"
        else -> "${line[i]}${cache.getOrPut(line.drop(i + 1) to size - 1) {
            joltage(line.drop(i + 1), size - 1)
          }}"
    }.toLong().coerceAtLeast(best)
  }
  return best
}
