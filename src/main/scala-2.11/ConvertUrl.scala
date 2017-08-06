import com.google.common.base.Charsets
import com.google.common.hash.Hashing
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response, Status, Version}
import com.twitter.util.Future
import org.bson.Document

class ConvertUrl(urls:MongoCollection[Document], prefix:String) extends Service[Request, Response] {

  def apply(request: Request) = {
    val long = request.params("url")
    shorten(long) find { short =>
      val findByShort = Filters.eq("_id", short)
      urls.find(findByShort).first() match {
        case existing: Document if !long.equals(existing.get("url")) =>
          // try next
          false
        case _: Document =>
          // nothing to do, just re-use
          true
        case _ =>
          urls.insertOne(new Document().append("_id", short).append("url", long))
          true
      }
    } match {
      case Some(short) =>
        val response = Response(Version.Http11, Status.Ok)
        response.contentString = prefix + short
        Future.value(response)
      case None =>
        Future.value(Response(Version.Http11, Status.InternalServerError))
    }
  }

  def shorten(longUrl : String) = {
    val sha1 = Hashing.sha1().hashString(longUrl, Charsets.UTF_8).toString
    for (i <- Range(0, sha1.length-8))
      yield sha1.substring(i, i+8)
  }
}

