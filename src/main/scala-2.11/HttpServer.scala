import java.net.InetSocketAddress

import com.google.common.base.Charsets
import com.google.common.io.Resources
import com.mongodb.{MongoClient, MongoClientURI}
import com.twitter.finagle.builder.{Server, ServerBuilder}
import com.twitter.finagle.http._
import com.twitter.finagle.{Http, Service, SimpleFilter}
import com.twitter.util.Future

object HttpServer {

  val mongo = new MongoClient(new MongoClientURI("mongodb://avpavlov:avpavlov@ds141937.mlab.com:41937/avpavlov-urls"))

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

  class Form extends Service[Request, Response] {
    def apply(request: Request) = {
      val response = Response(Version.Http11, Status.Ok)
      response.contentString = Resources.toString(HttpServer.getClass.getResource("form.html"), Charsets.UTF_8)
      Future.value(response)
    }
  }

  class Converter extends Service[Request, Response] {
    def apply(request: Request) = {
      val response = Response(Version.Http11, Status.Ok)
      response.contentString = "Converter " +request.path + " URL->"+  request.params("url")
      Future.value(response)
    }
  }

  class Redirector extends Service[Request, Response] {
    def apply(request: Request) = {
      val response = Response(Version.Http11, Status.Ok)
      response.contentString = "Redirector " +request.path
      Future.value(response)
    }
  }

  def router : Service[Request, Response] = new HttpMuxer()
      .withHandler("/c/", new Converter)
      .withHandler("/r/", new Redirector)
      .withHandler("/", new Form)

  def main(args: Array[String]) {
    val handleExceptions = new HandleExceptions

    val shortUrlsService: Service[Request, Response] = handleExceptions andThen router

    val port = if (args.length > 0) args(0).toInt else 8080
    val server: Server = ServerBuilder()
      .stack(Http.server)
      .bindTo(new InetSocketAddress(port))
      .name("httpserver")
      .build(shortUrlsService)
  }

}