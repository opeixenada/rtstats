package stats.core

import stats.dto.{Stats, Transaction}


/**
  * This class contains logic for updating and retrieving transaction statistics.
  *
  * With every new recent (not older than 60 sec) transaction it updates collection of statistics
  * values for every second of the next minute.
  *
  * Retrieving statistics takes 0(1) space (collection of not more than 60 elements) and O(1)
  * time (lookup in that collection).
  */
class Statistics(time: Time) {
  private var stats: Map[Long, Stats] = Map.empty

  /**
    * If transaction is recent, updates map of current stats. Returns `True` on
    * success and `False` if transaction is older than 60 seconds.
    */
  def addTransaction(transaction: Transaction): Boolean = {
    if (!isRecent(transaction, time.tsNow / 1000)) return false

    stats.synchronized {

      val recentStats = for (differenceSec <- Range(0, 60)) yield {
        val currentTsSec = time.tsNow / 1000 + differenceSec
        val currentStats = stats.get(currentTsSec) match {
          case Some(s) =>
            if (isRecent(transaction, currentTsSec)) s.add(transaction.amount)
            else s
          case _ =>
            Stats.fromValue(transaction.amount)
        }

        currentTsSec -> currentStats
      }

      stats = recentStats.toMap
    }

    true
  }

  /** Gets statistics for the current moment. */
  def getStatistics: Stats = {
    stats.getOrElse(time.tsNow / 1000, Stats.empty)
  }

  /**
    * Checks if transaction is not older than 60 seconds.
    *
    * @param transaction
    * @param ts in seconds
    */
  private def isRecent(transaction: Transaction, ts: Long): Boolean = {
    transaction.timestamp / 1000 > ts - 60
  }

}