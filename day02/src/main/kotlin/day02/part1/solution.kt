package day02.part1

import java.io.BufferedReader

/**
 * @author verwoerd
 * @since 02/12/2025
 */
fun day02Part1(input: BufferedReader): Any {
  val cache = mutableMapOf<Long, Boolean>()
  return input.readLine().split(",").map { it.split("-") }
    .flatMap { it.checkRange(cache, Long::isFake) }.sum()
}

fun List<String>.checkRange(cache: MutableMap<Long, Boolean>, check: Long.() -> Boolean) : List<Long> {
  val (start, end) = this
  val result = mutableListOf<Long>()
  for (i in start.toLong()..end.toLong()) {
    if(cache.computeIfAbsent(i) { i.check() }) {
      result += i
    }
  }
  return result
}

fun Long.isFake() : Boolean {
  val str = this.toString()
  val half = str.length / 2
  return str.take(half) == str.drop(half)
}
