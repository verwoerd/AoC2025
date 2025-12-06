package day06.part2

import rawMaze
import java.io.BufferedReader

/**
 * @author verwoerd
 * @since 06/12/2025
 */
fun day06Part2(input: BufferedReader): Any {
  val grid = input.rawMaze()
  val lineLength = grid.first().length
  val turned = (lineLength-1 downTo 0).map { i->
    grid.map {it.getOrElse(i, {' '})}
  }
  var sum = 0L
  val current = mutableListOf<Long>()
  turned.forEach {line ->
    val last = line.last()
    val value = line.dropLast(1).joinToString("")
    if(value.isBlank()) return@forEach
    current.add(value.trim().toLong())
    if(last != ' ') {
      sum += when(last) {
        '+' -> current.sum()
        else -> current.fold(1){acc, value -> acc * value}
      }
      current.clear()
    }
  }
  return sum
}
