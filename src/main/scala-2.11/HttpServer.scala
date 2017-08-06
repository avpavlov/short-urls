import java.net.InetSocketAddress

import com.twitter.finagle.builder.{Server, ServerBuilder}
import com.twitter.finagle.http._
import com.twitter.finagle.{Http, Service, SimpleFilter}
import com.twitter.util.Future

object HttpServer {

  class HandleExceptions extends SimpleFilter[Request, Response] {
    def apply(request: Request, service: Service[Request, Response]) = {

      service(request) handle {
        case error =>
          val statusCode = error match {
            case _: IllegalArgumentException =>
              Status.Forbidden
            case _ =>
              Status.InternalServerError
          }
          val errorResponse = Response(Version.Http11, statusCode)
          errorResponse.contentString = error.getStackTrace.mkString("\n")

          errorResponse
      }
    }
  }

  class Respond extends Service[Request, Response] {
    def apply(request: Request) = {
      val response = Response(Version.Http11, Status.Ok)
      response.contentString = "hello world"
      Future.value(response)
    }
  }

  def main(args: Array[String]) {
    val handleExceptions = new HandleExceptions
    val respond = new Respond

    val shortUrlsService: Service[Request, Response] = handleExceptions andThen respond

    val port = if (args.length > 0) args(0).toInt else 8080
    val server: Server = ServerBuilder()
      .stack(Http.server)
      .bindTo(new InetSocketAddress(port))
      .name("httpserver")
      .build(shortUrlsService)
  }
}