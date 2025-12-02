package day02.part2

import day02.part1.checkRange
import java.io.BufferedReader

/**
 * @author verwoerd
 * @since 02/12/2025
 */
fun day02Part2(input: BufferedReader): Any {
  val cache = mutableMapOf<Long, Boolean>()
  return input.readLine().split(",").map { it.split("-") }
    .flatMap { it.checkRange(cache, Long::isFake) }.sum()
}

fun Long.isFake() : Boolean {
  val str = this.toString()
  val half = str.length / 2
  for (i in half downTo  1) {
    val pattern = str.take(i)
    if(str.length % i != 0) continue
    if(str.windowed(size = i, step = i).all { it == pattern }) {
      return true
    }
  }
  return false
}
