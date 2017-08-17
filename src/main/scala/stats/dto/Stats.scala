package stats.dto


case class Stats(sum: Double, avg: Double, max: Double, min: Double, count: Int) {
  def add(value: Double): Stats = {
    val newSum = sum + value
    val newCount = count + 1
    val newAvg = newSum / newCount
    val newMax = math.max(max, value)
    val newMin = math.min(min, value)
    Stats(newSum, newAvg, newMax, newMin, newCount)
  }
}

object Stats {
  def empty: Stats = Stats(0, 0, 0, 0, 0)
  def fromValue(value: Double): Stats = Stats(value, value, value, value, 1)
}

