package day06.part1

import java.io.BufferedReader

/**
 * @author verwoerd
 * @since 06/12/2025
 */
fun day06Part1(input: BufferedReader): Any {
  val groups = input.readLines().map { it.split("\\s+".toRegex()).filter { it.isNotEmpty() } }
  return (0..<groups.first().size).sumOf{ i->
    val operand = groups.last()[i]
    val start = when{
      operand == "+" -> 0L
      else -> 1L
    }
    groups.dropLast(1).fold(start) { acc, next ->
      when {
        start == 1L -> acc*next[i].toLong()
        else -> acc+next[i].toLong()
      }
    }

  }
}
