package stats.http

import com.twitter.finagle.Service
import com.twitter.finagle.http.path.{/, Path, Root}
import com.twitter.finagle.http.{Method, Request, Response, Status}
import com.twitter.util.Future
import org.json4s.native.Serialization.write
import stats.core.{Statistics, Time, TimeImpl}
import stats.dto.Transaction
import stats.errors.ClientError
import stats.utils.HttpUtils
import stats.utils.HttpUtils._


/** Finagle HTTP service for calculating statistics. */
class StatsService(time: Time = TimeImpl) extends Service[Request, Response] {

  private val statistics = new Statistics(time)

  override def apply(request: Request): Future[Response] = {
    Path(request.path) match {
      case Root / "transactions" =>
        request.method match {
          case Method.Post => postTransaction(request)
          case _ => throw new ClientError("Unsupported method")
        }

      case Root / "statistics" =>
        request.method match {
          case Method.Get => getStatistics
          case _ => throw new ClientError("Unsupported method")
        }

      case _ => Future.value(Response(Status(404)))
    }
  }

  private def postTransaction(request: Request): Future[Response] = {
    Future.apply {
      val transaction = HttpUtils.parseJsonFromHttpMessage[Transaction](request)
      val status = if (statistics.addTransaction(transaction)) 201 else 204
      val response = Response(Status(status))
      response
    }
  }

  private def getStatistics: Future[Response] = {
    Future.apply {
      val stats = statistics.getStatistics

      val response = new Response.Ok()
      response.setContentTypeJson()
      response.setContentString(write(stats)(formats = defaultFormats))
      response
    }
  }
}