import com.google.common.base.Charsets
import com.google.common.io.Resources
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response, Status, Version}
import com.twitter.util.Future

class ShowForm extends Service[Request, Response] {
  def apply(request: Request) = {
    val response = Response(Version.Http11, Status.Ok)
    response.contentString = Resources.toString(HttpServer.getClass.getResource("form.html"), Charsets.UTF_8)
    Future.value(response)
  }
}

