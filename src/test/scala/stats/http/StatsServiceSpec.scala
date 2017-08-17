package stats.http

import com.twitter.finagle.http.{RequestBuilder, Status}
import com.twitter.io.Buf
import org.scalactic.TolerantNumerics
import org.scalatest.{FunSpec, Matchers}
import stats.core.Time
import stats.dto.Stats
import stats.utils.HttpUtils


class StatsServiceSpec extends FunSpec with Matchers {
  describe("StatsService") {
    implicit val doubleEquality = TolerantNumerics.tolerantDoubleEquality(0.001)

    def reqTransaction(json: String) = RequestBuilder()
      .url("http://localhost:8000/transactions")
      .setHeader("Content-Type", "application/json")
      .buildPost(Buf.Utf8(json))

    val reqStatistics = RequestBuilder()
      .url("http://localhost:8000/statistics")
      .buildGet()

    val time = new Time {
      def tsNow: Long = "1478192204001".toLong
    }

    it("should return 201 if transaction is recent") {
      val service = new StatsService(time)

      val json =
        """
        {
          "amount": 12.3,
          "timestamp": 1478192204000
        }
        """

      service(reqTransaction(json))
        .onFailure { _ => fail() }
        .onSuccess { _.status should equal(Status(201)) }
    }

    it("should return 204 if transaction is old") {
      val service = new StatsService(time)

      val json =
        """
        {
          "amount": 12.3,
          "timestamp": 1478192144001
        }
        """

      service(reqTransaction(json))
        .onFailure { _ => fail() }
        .onSuccess { _.status should equal(Status(204)) }
    }

    it("should return stats") {
      val service = new StatsService(time)

      val json =
        """
        {
          "amount": 12.3,
          "timestamp": 1478192204000
        }
        """

      val result = Stats(12.3, 12.3, 12.3, 12.3, 1)

      (for {
        _ <- service(reqTransaction(json))
        response <- service(reqStatistics)
      } yield response)
        .onFailure { _ => fail() }
        .onSuccess { r => HttpUtils.parseJsonFromHttpMessage[Stats](r) should equal(result) }
    }
  }
}

