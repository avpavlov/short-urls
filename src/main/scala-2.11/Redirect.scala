import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response, Status, Version}
import com.twitter.util.Future
import org.bson.Document

class Redirect(urls: MongoCollection[Document]) extends Service[Request, Response] {

  def apply(request: Request) = {
    val extractor = """.+/([^/]{8,})$""".r
    request.path match {
      case extractor(short) =>
        urls.find(Filters.eq("_id", short)).first() match {
          case existing: Document =>
            val response = Response(Version.Http11, Status.MovedPermanently)
            response.headerMap("Location") = existing.get("url").toString
            Future.value(response)
          case _ =>
            Future.value(Response(Version.Http11, Status.NotFound))
        }
      case _ =>
        Future.value(Response(Version.Http11, Status.NotAcceptable))
    }
  }
}

