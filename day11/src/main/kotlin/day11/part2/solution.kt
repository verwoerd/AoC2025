package day11.part2

import day11.part1.readNetwork
import java.io.BufferedReader

/**
 * @author verwoerd
 * @since 11/12/2025
 */
fun day11Part2(input: BufferedReader): Any {
  val network = input.readNetwork()
  val start = "svr"
  data class ModulePathCount(val none: Long = 0, val dac: Long = 0, val fft: Long = 0, val both: Long = 0) {
    operator fun plus(other: ModulePathCount) = ModulePathCount(
      none + other.none, dac + other.dac, fft + other.fft, both + other.both
    )
  }
  val cache = mutableMapOf<String, ModulePathCount>()
  fun computePath(from: String): ModulePathCount {
    if (from == "out") {
      return ModulePathCount(none = 1)
    }
    return cache.getOrPut(from) {
      network.getValue(from).fold(ModulePathCount()) { acc, next ->
        val child = computePath(next)
        acc + when (from) {
          "dac" -> ModulePathCount(none = 0, child.none + child.dac, fft = 0, child.fft + child.both)
          "fft" -> ModulePathCount(none = 0, dac = 0, child.none + child.fft, child.dac + child.both)
          else -> child
        }
      }
    }
  }
  return computePath(start).both
}
