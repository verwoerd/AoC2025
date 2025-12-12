package day12.part1

import Coordinate
import FourDirections
import java.io.BufferedReader
import kotlin.math.max

const val CHEESE = false

/**
 * @author verwoerd
 * @since 12/12/2025
 */
fun day12Part1(input: BufferedReader): Any {
  val (blocks, grids) = input.parse()
  if (CHEESE) {
    return grids.count { it.width * it.height >= it.block1 * 9 + it.block2 * 9 + it.block3 * 9 + it.block4 * 9 + it.block5 * 9 + it.block6 * 9 }
  }
  return grids.count { grid ->
    val result = canFit(grid, blocks)
    result
  }
}

fun BufferedReader.parse(): Pair<MutableList<Block>, MutableList<Grid>> {
  val gridSpecRegex = Regex("""(\d+)x(\d+): (\d+) (\d+) (\d+) (\d+) (\d+) (\d+)""")
  val allLines = this.readLines()
  val blocks = mutableListOf<Block>()
  var lineIndex = 0
  while (allLines[lineIndex].endsWith(':')) {
    val blockIndex = allLines[lineIndex].dropLast(1).toInt()
    val topBlock = allLines[lineIndex + 1]
    val midBlock = allLines[lineIndex + 2]
    val bottomBlock = allLines[lineIndex + 3]
    blocks.add(Block(blockIndex, topBlock, midBlock, bottomBlock))
    lineIndex += 5
  }
  val grids = mutableListOf<Grid>()
  while (lineIndex in allLines.indices) {
    val line = allLines[lineIndex]
    gridSpecRegex.matchEntire(line)!!.groupValues.drop(1).let { values ->
      grids.add(
        Grid(
          values[0].toInt(),
          values[1].toInt(),
          values[2].toInt(),
          values[3].toInt(),
          values[4].toInt(),
          values[5].toInt(),
          values[6].toInt(),
          values[7].toInt()
        )
      )
    }
    lineIndex++
  }
  return blocks to grids
}


data class Grid(
  val width: Int,
  val height: Int,
  val block1: Int,
  val block2: Int,
  val block3: Int,
  val block4: Int,
  val block5: Int,
  val block6: Int,
)

data class Block(
  val index: Int,
  var coordinatesByDirection: Map<FourDirections, List<Coordinate>> = mutableMapOf(),
) {
  constructor(index: Int, topLine: String, midLine: String, bottomLine: String) : this(index) {
    val shapeCoordinates = topLine.toCoordinates(0) + midLine.toCoordinates(1) + bottomLine.toCoordinates(2)
    this.coordinatesByDirection = FourDirections.entries.associateWith { rotate(shapeCoordinates, it) }
  }
}

fun String.toCoordinates(y: Int): List<Coordinate> =
  withIndex().filter { it.value == '#' }.map { Coordinate(it.index, y) }

fun rotate(mask: List<Coordinate>, rotation: FourDirections): List<Coordinate> = when (rotation) {
  FourDirections.UP -> mask
  FourDirections.RIGHT -> mask.map { (x, y) -> Coordinate(2 - y, x) }
  FourDirections.DOWN -> mask.map { (x, y) -> Coordinate(2 - x, 2 - y) }
  FourDirections.LEFT -> mask.map { (x, y) -> Coordinate(y, 2 - x) }
}


data class Placement(val type: Int, val mask: LongArray, val area: Int) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Placement

    if (type != other.type) return false
    if (area != other.area) return false
    if (!mask.contentEquals(other.mask)) return false

    return true
  }

  override fun hashCode(): Int {
    var result = type
    result = 31 * result + area
    result = 31 * result + mask.contentHashCode()
    return result
  }
}


fun canFit(grid: Grid, blocks: List<Block>): Boolean {
  val width = grid.width
  val height = grid.height
  val cellCount = width * height
  val wordCount = (cellCount + 63) ushr 6

  val remaining = intArrayOf(
    grid.block1, grid.block2, grid.block3, grid.block4, grid.block5, grid.block6
  )

  val areaPerType = IntArray(blocks.size) { type ->
    blocks[type].coordinatesByDirection[FourDirections.UP]!!.size
  }

  fun neededArea(): Int {
    var sum = 0
    for (i in remaining.indices) sum += remaining[i] * areaPerType[i]
    return sum
  }

  fun normalize(coords: List<Coordinate>): List<Coordinate> {
    var minX = Int.MAX_VALUE
    var minY = Int.MAX_VALUE
    for ((x, y) in coords) {
      if (x < minX) minX = x
      if (y < minY) minY = y
    }
    return coords.map { (x, y) -> Coordinate(x - minX, y - minY) }
  }

  fun flipX(coords: List<Coordinate>): List<Coordinate> {
    var maxX = Int.MIN_VALUE
    for ((x, _) in coords) if (x > maxX) maxX = x
    return coords.map { (x, y) -> Coordinate(maxX - x, y) }
  }

  fun key(coords: List<Coordinate>): String =
    coords.sortedWith(compareBy<Coordinate> { it.y }.thenBy { it.x }).joinToString("|") { "${it.x},${it.y}" }

  fun allVariantsForType(t: Int): List<List<Coordinate>> {
    val baseUp = blocks[t].coordinatesByDirection[FourDirections.UP]!!
    val candidates = ArrayList<List<Coordinate>>(8)

    val rotations = FourDirections.entries.map { dir -> blocks[t].coordinatesByDirection[dir]!! }
    for (r in rotations) candidates.add(normalize(r))

    val flippedBase = normalize(flipX(baseUp))
    for (dir in FourDirections.entries) {
      candidates.add(normalize(rotate(flippedBase, dir)))
    }

    val seen = HashSet<String>(candidates.size * 2)
    val out = ArrayList<List<Coordinate>>(candidates.size)
    for (c in candidates) {
      val k = key(c)
      if (seen.add(k)) out.add(c)
    }
    return out
  }

  fun toMask(coords: List<Coordinate>, ox: Int, oy: Int): LongArray {
    val maskWords = LongArray(wordCount)
    for ((dx, dy) in coords) {
      val x = ox + dx
      val y = oy + dy
      val idx = y * width + x
      val w = idx ushr 6
      val b = idx and 63
      maskWords[w] = maskWords[w] or (1L shl b)
    }
    return maskWords
  }

  val placementsByType: Array<MutableList<Placement>> = Array(blocks.size) { mutableListOf() }

  for (t in blocks.indices) {
    val variants = allVariantsForType(t)
    val area = variants.firstOrNull()?.size ?: 0
    if (area == 0) continue

    for (shape in variants) {
      var maxX = 0
      var maxY = 0
      for ((x, y) in shape) {
        maxX = max(maxX, x)
        maxY = max(maxY, y)
      }
      val maxOx = width - 1 - maxX
      val maxOy = height - 1 - maxY
      if (maxOx < 0 || maxOy < 0) continue

      for (oy in 0..maxOy) {
        for (ox in 0..maxOx) {
          placementsByType[t].add(Placement(t, toMask(shape, ox, oy), area))
        }
      }
    }
  }

  for (t in remaining.indices) {
    if (remaining[t] > 0 && placementsByType[t].isEmpty()) return false
  }

  class Occupancy {
    val bits = LongArray(wordCount)
    var usedCells = 0
  }

  class UndoStack(capacity: Int) {
    val idx = IntArray(capacity * 2)
    val old = LongArray(capacity)
    var topPairs = 0

    fun mark(): Int = topPairs

    fun record(i: Int, oldValue: Long) {
      idx[topPairs] = i
      old[topPairs] = oldValue
      topPairs++
    }

    fun rollback(occ: Occupancy, mark: Int, beforeUsedCells: Int) {
      for (p in topPairs - 1 downTo mark) {
        val i = idx[p]
        occ.bits[i] = old[p]
      }
      occ.usedCells = beforeUsedCells
      topPairs = mark
    }
  }


  fun hashState(occ: Occupancy, remainingCounts: IntArray): Long {
    var h = 1469598103934665603L
    for (w in occ.bits) {
      h = (h xor w) * 1099511628211L
    }
    for (v in remainingCounts) {
      h = (h xor v.toLong()) * 1099511628211L
    }
    return h
  }

  val seenFail = HashSet<Long>(1 shl 16)
  val occupancy = Occupancy()
  val undo = UndoStack(capacity = 1_000_000.coerceAtMost(placementsByType.sumOf { it.size }))
  val freeCellsTotal = cellCount

  fun dfs(): Boolean {
    if (!remaining.any { it > 0 }) return true

    val needed = neededArea()
    val free = freeCellsTotal - occupancy.usedCells
    if (needed > free) return false

    val stateHash = hashState(occupancy, remaining)
    if (stateHash in seenFail) return false

    var bestType = -1
    var bestCount = Int.MAX_VALUE

    for (t in remaining.indices) {
      if (remaining[t] <= 0) continue
      var viableCount = 0
      val placementsForType = placementsByType[t]
      for (p in placementsForType) {
        var ok = true
        val placementMask = p.mask
        for (i in 0..<wordCount) {
          if ((occupancy.bits[i] and placementMask[i]) != 0L) {
            ok = false
            break
          }
        }
        if (ok) {
          viableCount++
          if (viableCount >= bestCount) break
        }
      }
      if (viableCount == 0) {
        seenFail.add(stateHash)
        return false
      }
      if (viableCount < bestCount) {
        bestCount = viableCount
        bestType = t
        if (bestCount == 1) break
      }
    }

    val placementsForBestType = placementsByType[bestType]

    outer@for (p in placementsForBestType) {
      val placementMask = p.mask
      for (i in 0..<wordCount) {
        if ((occupancy.bits[i] and placementMask[i]) != 0L) {
          continue@outer
        }
      }

      val mark = undo.mark()
      val beforeUsed = occupancy.usedCells
      for (i in 0..<wordCount) {
        val maskWord = placementMask[i]
        if (maskWord == 0L) continue
        val before = occupancy.bits[i]
        val after = before or maskWord
        if (after != before) undo.record(i, before)
        occupancy.bits[i] = after
      }
      occupancy.usedCells = beforeUsed + p.area

      remaining[bestType]--
      if (dfs()) return true
      remaining[bestType]++
      undo.rollback(occupancy, mark, beforeUsed)
    }
    seenFail.add(stateHash)
    return false
  }

  return dfs()
}
