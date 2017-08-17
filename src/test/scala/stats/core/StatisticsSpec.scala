package stats.core

import com.twitter.util.Future
import org.scalatest.{FunSpec, Matchers}
import stats.dto.{Stats, Transaction}


class StatisticsSpec extends FunSpec with Matchers {

  describe("Statistics") {

    val ts0 = "1478192204000".toLong

    val time = new Time {
      def tsNow: Long = ts0
    }

    it("should calculate stats for 1 transaction") {
      val statistics = new Statistics(time)

      val t1 = Transaction(12.3, ts0 - 1)

      val res = Stats(12.3, 12.3, 12.3, 12.3, 1)

      statistics.addTransaction(t1)
      statistics.getStatistics should equal(res)
    }

    it("should calculate stats for 5 transactions") {
      val statistics = new Statistics(time)

      val ts = List(
        Transaction(12.3, ts0 - 5),
        Transaction(12.4, ts0 - 4),
        Transaction(12.5, ts0 - 3),
        Transaction(12.6, ts0 - 2),
        Transaction(12.7, ts0 - 1))

      val res = Stats(62.5, 12.5, 12.7, 12.3, 5)

      ts.foreach(statistics.addTransaction)
      statistics.getStatistics should equal(res)
    }

    it("shouldn't include old transactions") {
      val statistics = new Statistics(time)

      val ts = List(
        Transaction(12.3, ts0 - 5),
        Transaction(12.4, ts0 - 4),
        Transaction(12.5, ts0 - 60 * 1000),
        Transaction(12.6, ts0 - 2),
        Transaction(12.7, ts0 - 1))

      ts.foreach(statistics.addTransaction)
      statistics.getStatistics.count should equal(4)
    }

    it("should return empty Stats if there are no recent transactions") {
      val statistics = new Statistics(time)

      val ts = List(
        Transaction(12.5, ts0 - 60 * 1000 - 1),
        Transaction(12.7, ts0 - 60 * 1000))

      ts.foreach(statistics.addTransaction)
      statistics.getStatistics.count should equal(0)
    }

    it("should calculate stats with parallel requests") {
      val statistics = new Statistics(time)

      val fs = 1 to 10 map { x =>
        val t = Transaction(x, ts0 - x)
        Future.apply(statistics.addTransaction(t))
      }

      Future.join(fs)
        .onFailure { _ => fail() }
        .onSuccess { _ => statistics.getStatistics.count should equal(10) }
    }
  }

}

