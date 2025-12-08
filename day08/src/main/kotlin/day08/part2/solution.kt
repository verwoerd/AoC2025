package day08.part2

import day08.part1.connectJunctionBoxes
import java.io.BufferedReader

/**
 * @author verwoerd
 * @since 08/12/2025
 */
fun day08Part2(input: BufferedReader): Any {
  return connectJunctionBoxes(input, Int.MAX_VALUE).second
}
