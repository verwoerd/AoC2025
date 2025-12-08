package day08.part1

import TripleCoordinate
import euclideanDistance
import priorityQueueOf
import java.io.BufferedReader

/**
 * @author verwoerd
 * @since 08/12/2025
 */
fun day08Part1(input: BufferedReader, connections: Int = 1000): Any {
  val (dus) = connectJunctionBoxes(input, connections)
  return dus.getThreeLargestClusterSizes().fold(1L){acc, i -> acc*i}
}

fun connectJunctionBoxes(input: BufferedReader, connections: Int = 1000): Pair<DisjointUnionSet, Any> {
  val points = input.lineSequence().map { c ->
    val (x, y, z) = c.split(",").map { it.toLong() }
    TripleCoordinate(x,y,z)
  }.toList()
  val reverseIndexMap = points.withIndex().associate { (i,v) -> v to i }

  val comparator: Comparator<Triple<Double, TripleCoordinate, TripleCoordinate>> = compareBy { it.first }
  val queue = priorityQueueOf(comparator)
  points.flatMapIndexed { index, a ->
    points.drop(index+1).map{b -> Triple(euclideanDistance(a,b), a,b)}
  }.toCollection(queue)
  val dus = DisjointUnionSet(points.size)
  repeat(connections) { i ->
    val (_,a,b) = queue.poll()
    val indexA = reverseIndexMap.getValue(a)
    val indexB = reverseIndexMap.getValue(b)
    dus.union(indexA, indexB)
    if(dus.count == 1) {
      return dus to a.x*b.x
    }
  }
  return dus to -1
}


class DisjointUnionSet(size: Int) {
  private val parent = IntArray(size)
  private val rank = ByteArray(size)
  var count = size
    private set

  init {
    parent.indices.forEach { parent[it] = it }
  }

  fun connected(v: Int, w:Int) = find(v) == find(w)

  fun find(v: Int): Int {
    var current = v
    while(parent[current] != current) {
      parent[current] = parent[parent[current]]
      current = parent[current]
    }
    return current
  }

  fun union(v: Int, w: Int) {
    val rootV = find(v)
    val rootW = find(w)
    when {
      rootV == rootW -> return
      rank[rootV] > rank[rootW] -> parent[rootW] = rootV
      rank[rootW] > rank[rootV] -> parent[rootV] = rootW
      else -> {
        parent[rootV] = rootW
        rank[rootW]++
      }
    }
    count --
  }

  fun getThreeLargestClusterSizes(): List<Int> {
    val sizes = IntArray(parent.size)
    parent.indices.forEach {
      sizes[find(it)]++
    }
    return sizes.filter { it > 0 }.sortedDescending().take(3)
  }
}
