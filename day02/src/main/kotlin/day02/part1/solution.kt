package day02.part1

import java.io.BufferedReader

/**
 * @author verwoerd
 * @since 02/12/2025
 */
fun day02Part1(input: BufferedReader): Any = input.parseRanges().sumOf { it.checkRange(Long::isFake) }

fun BufferedReader.parseRanges() = readLine().split(",").map { it.split("-") }

fun List<String>.checkRange(check: Long.() -> Boolean): Long =
  (get(0).toLong()..get(1).toLong()).filter { it.check() }.sum()

fun Long.isFake(): Boolean {
  val str = this.toString()
  val half = str.length / 2
  return str.take(half) == str.drop(half)
}
