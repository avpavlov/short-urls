import java.net.InetSocketAddress

import com.mongodb.{MongoClient, MongoClientURI}
import com.twitter.finagle.builder.{Server, ServerBuilder}
import com.twitter.finagle.http._
import com.twitter.finagle.{Http, Service, SimpleFilter}

object HttpServer {

  val mongo = new MongoClient(new MongoClientURI("mongodb://avpavlov:avpavlov@ds141937.mlab.com:41937/avpavlov-urls"))
  val urls = mongo.getDatabase("avpavlov-urls").getCollection("shorturls")

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


  def main(args: Array[String]) {
    val handleExceptions = new HandleExceptions

    val (host,port) = args(0).split(":") match {
      case Array(h,p) => (h, p.toInt)
      case Array(h) => (h, 80)
      case _ => throw new IllegalArgumentException("Program expects hostname[:port] argument")
    }

    val (urlHost,urlPort) = if (args.length > 1)
      args(1).split(":") match {
        case Array(h,p) => (h, p.toInt)
        case Array(h) => (h, 80)
        case _ => throw new IllegalArgumentException("Program expects hostname[:port] argument")
      } else {
        (host,port)
      }

    val redirectMapping = "/r/"
    val redirectPrefix = urlPort match {
      case 80 => "http://"+host+redirectMapping
      case _ => "http://"+urlHost+":"+urlPort+redirectMapping
    }

    val router : Service[Request, Response] = new HttpMuxer()
      .withHandler("/c/", new ConvertUrl(urls, redirectPrefix))
      .withHandler(redirectMapping, new Redirect(urls))
      .withHandler("/", new ShowForm)

    val shortUrlsService: Service[Request, Response] = handleExceptions andThen router

    val server: Server = ServerBuilder()
      .stack(Http.server)
      .bindTo(new InetSocketAddress(host, port))
      .name("httpserver")
      .build(shortUrlsService)
  }

}