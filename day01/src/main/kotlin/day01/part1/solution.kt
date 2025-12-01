package day01.part1

import java.io.BufferedReader

/**
 * @author verwoerd
 * @since 01/12/2025
 */
fun day01Part1(input: BufferedReader): Any {
  return input.numberSequences().count { it == 0 }
}

fun BufferedReader.numberSequences() = readLines().map { line ->
  when {
    line.startsWith('L') -> (line.drop(1).toInt()* -1).mod(100)
    else -> line.drop(1).toInt()
  }
}.runningFold(50) { acc, i ->
  (acc + i) % 100
}
