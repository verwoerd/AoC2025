package day05.part2

import priorityQueueOf
import java.io.BufferedReader
import java.math.BigInteger

/**
 * @author verwoerd
 * @since 05/12/2025
 */
fun day05Part2(input: BufferedReader): Any {
  val lines = input.readLines()
  val comparator: Comparator<ClosedRange<BigInteger>> = Comparator.comparing { it.start }
  val queue = priorityQueueOf(comparator)

  lines.takeWhile { it.isNotEmpty() }.map { line ->
    val (a, b) = line.split("-").map { it.toBigInteger() }
    a..b
  }.toCollection(queue)
  val finished = mutableListOf<ClosedRange<BigInteger>>()
  while (queue.isNotEmpty()) {
    val i = queue.poll()
    // find if a range that fully contains the range
    if (finished.any { i.start in it && i.endInclusive in it }) continue

    val partialStartOverlap = finished.firstOrNull { i.start in it }
    if (partialStartOverlap != null) {
      val next = partialStartOverlap.endInclusive + BigInteger.ONE..i.endInclusive
      queue.add(next)
      continue
    }
    val partialEndOverlap = finished.firstOrNull { i.endInclusive in it }
    if (partialEndOverlap != null) {
      val next = i.start..partialEndOverlap.start - BigInteger.ONE
      queue.add(next)
      continue
    }
    finished.add(i)
  }
  check(finished.sortedBy { it.start }.zipWithNext().none { (a, b) -> a.endInclusive in b || b.endInclusive in a })


  return finished.sumOf { it.endInclusive - it.start + BigInteger.ONE }
}
