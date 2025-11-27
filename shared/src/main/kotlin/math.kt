import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun lcm(number1: Long, number2: Long): Long {
  if (number1 == 0L || number2 == 0L) return 0
  val absHigherNumber = absoluteMax(number1, number2)
  val absLowerNumber = absoluteMin(number1, number2)
  var lcm = absHigherNumber
  while (lcm % absLowerNumber != 0L) lcm += absHigherNumber
  return lcm
}

fun absoluteMax(num1: Long, num2: Long) = max(abs(num1), abs(num2))

fun absoluteMin(num1: Long, num2: Long) = min(abs(num1), abs(num2))

fun List<Long>.lcmForList(): Long = fold(1L) { lcmOfAllNumbers, number ->
  val result = lcm(lcmOfAllNumbers, number)
  if (result == 0L) return 0
  result
}

fun gcd(number1: Long, number2: Long): Long = when (number2) {
  0L -> number1
  else -> gcd(number2, number1 % number2)
}

fun crossProduct(a: Coordinate, b: Coordinate) = a.x.toLong() * b.y.toLong() - b.x.toLong() * a.y.toLong()

fun List<Coordinate>.polygonArea(): Double =
  abs((1..<size).sumOf { it -> crossProduct(get(it), get(it - 1)) } / 2.0)

fun List<Coordinate>.pointsOnBoundary(): Long =
  zipWithNext().sumOf { (a, b) ->
    val delta = a - b
    abs(gcd(delta.x.toLong(), delta.y.toLong()))
  }.let {
    val delta = last() - first()
    abs(gcd(delta.x.toLong(), delta.y.toLong())) + it
  }
