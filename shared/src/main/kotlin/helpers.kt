import java.util.PriorityQueue
import kotlin.math.ceil

/**
 * @author verwoerd
 * @since 09-11-20
 */
fun Boolean.toInt() = when {
  this -> 1
  else -> 0
}

infix fun Long.ceilDivision(b: Number) = ceil(this / b.toDouble()).toLong()
infix fun Int.ceilDivision(b: Number) = ceil(this / b.toDouble()).toInt()

fun <T: Any> priorityQueueOf(vararg args: T): PriorityQueue<T> {
  val queue = PriorityQueue<T>()
  queue.addAll(args)
  return queue
}

fun <T: Any> priorityQueueOf(comparator: Comparator<T>, vararg args: T): PriorityQueue<T> {
  val queue = PriorityQueue<T>(comparator)
  queue.addAll(args)
  return queue
}

fun Char.toIntValue() = code - '0'.code
fun Char.toLongValue():Long = code.toLong() - '0'.code
