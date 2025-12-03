package day03.part1

import toIntValue
import java.io.BufferedReader

/**
 * @author verwoerd
 * @since 02/12/2025
 */
fun day03Part1(input: BufferedReader): Any {
  return input.readLines().sumOf { joltage(it) }
}

fun joltage(line: String): Int {
  val first = line.dropLast(1).withIndex().maxByOrNull { it.value }!!
  val second = line.drop(first.index + 1).withIndex().maxByOrNull { it.value }!!
  return first.value.toIntValue()*10 + second.value.toIntValue()
}
