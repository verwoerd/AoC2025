package day02.part2

import day02.part1.checkRange
import day02.part1.parseRanges
import java.io.BufferedReader

/**
 * @author verwoerd
 * @since 02/12/2025
 */
fun day02Part2(input: BufferedReader): Any = input.parseRanges().sumOf { it.checkRange(Long::isFake) }

fun Long.isFake(): Boolean {
  val str = this.toString()
  val half = str.length / 2
  for (i in half downTo 1) {
    if (str.length % i != 0) continue
    val pattern = str.take(i)
    if (str.windowed(size = i, step = i).all { it == pattern }) return true
  }
  return false
}
