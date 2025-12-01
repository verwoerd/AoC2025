package day01.part2

import ceilDivision
import java.io.BufferedReader

/**
 * @author verwoerd
 * @since 01/12/2025
 */
fun day01Part2(input: BufferedReader): Any {
  var count = 0
  input.readLines().map { line ->
    when {
      line.startsWith('L') -> (line.drop(1).toInt() * -1)
      else -> line.drop(1).toInt()
    }
  }.fold(50) { acc, i ->
    var next : Int
    when {
        i >= 0 -> {
            next = acc + i
            count += next / 100
        }
        else -> {
            count -= i / 100
            next = acc + i % 100
            if (acc != 0 && next <= 0) count++
        }
    }
    next.mod(100)
  }
  return count
}
