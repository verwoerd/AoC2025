package day10.part1

import org.ojalgo.optimisation.ExpressionsBasedModel
import org.ojalgo.optimisation.integer.IntegerSolver
import org.ojalgo.type.context.NumberContext
import priorityQueueOf
import toInt
import java.io.BufferedReader
import java.math.BigDecimal


/**
 * @author verwoerd
 * @since 10/12/2025
 */
fun day10Part1(input: BufferedReader): Any {
  val puzzle = input.lineSequence().map { line -> Machine.parse(line) }.toList()
  return puzzle.sumOf { it.findShortestOpeningSequence() }
}

fun List<Int>.toBitMask() = fold(0) { acc, value -> acc or (1 shl value) }


data class Machine(
  val switches: Int, val joltage: List<Int>, val bitOperations: List<Int>, val operations: List<List<Int>>
) {
  companion object {
    fun parse(line: String): Machine {
      val groups = line.split(" ")
      val switches = groups.first().drop(1).dropLast(1).map { it == '#' }.reversed().fold(0) { acc, bool ->
        (acc shl 1) or bool.toInt()
      }
      val joltage = groups.last().dropLast(1).drop(1).split(',').map { it.toInt() }
      val operations = groups.drop(1).dropLast(1).map { o ->
        o.drop(1).dropLast(1).split(",").map { it.toInt() }
      }
      return Machine(switches, joltage, operations.map { it.toBitMask() }, operations)
    }

    val comparator: Comparator<Pair<Int, Set<Int>>> = compareBy { it.second.size }
  }

  fun findShortestOpeningSequence(): Int {
    val queue = priorityQueueOf(comparator)
    queue.add(0 to emptySet())
    val seen = mutableSetOf<Set<Int>>()
    while (queue.isNotEmpty()) {
      val (current, turns) = queue.poll()
      if (!seen.add(turns)) continue
      val next = bitOperations.map { current xor it to turns + it }.filter { it.second !in seen }.distinct()
      if (next.any { it.first == switches }) return turns.size + 1
      next.toCollection(queue)
    }
    error("Did not find target for $this")
  }

  fun findShortestJoltageSequence(): BigDecimal {
    val model = ExpressionsBasedModel()
    model.options.progress(IntegerSolver::class.java)
    model.options.solution = NumberContext.of(joltage.size)
    model.options.integer()

    // x_i = number of times operatoin i is applied
    val factors = Array(operations.size) {
      model.addVariable("x_${it}").integer().lower(0).weight(1) // weight for the objective function to minimize
    }
    joltage.indices.forEach { index ->
      val expr = model.addExpression("press_$index").level(joltage[index]) // level means exactly
      expr.setLinearFactorsSimple(operations.indices.filter { index in operations[it] }.map { factors[it] })
    }

    val result = model.minimise()
    return (0..<result.size()).sumOf { i ->
      result.get(i.toLong())
    }
  }
}
