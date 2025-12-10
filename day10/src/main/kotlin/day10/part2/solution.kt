package day10.part2

import day10.part1.Machine
import java.io.BufferedReader

/**
 * @author verwoerd
 * @since 10/12/2025
 */
fun day10Part2(input: BufferedReader): Any {
  return input.lineSequence().map { line -> Machine.parse(line) }.sumOf { it.findShortestJoltageSequence() }
}
