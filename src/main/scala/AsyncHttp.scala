package pomu.dispatch.gae.async

import dispatch._
import com.google.appengine.api.urlfetch._
import org.apache.http.{HttpHost,HttpRequest,HttpResponse,HttpEntity,ProtocolVersion}
import org.apache.http.message.{BasicHttpResponse}
import org.apache.http.entity.{ByteArrayEntity}
import org.apache.http.client.methods._
import collection.JavaConversions._

/**
 * call http using URLFetchService.fetchAsync
 */
trait Http extends HttpExecutor with BlockingCallback {
  /** result type */
  type HttpPackage[T] = FutureResponseWrapper[T]
  val UFS = URLFetchServiceFactory.getURLFetchService
  
  override def execute[T](host: HttpHost, 
                 creds: Option[Credentials], 
                 req: HttpRequestBase, 
                 block: HttpResponse => T,
                 listener: ExceptionListener): HttpPackage[T] = {
    val res = UFS.fetchAsync(make_request(host, req, fetchOptions))
    new FutureResponseWrapper(res, block, listener)
  }
  
  /**
   * override this to assign FetchOptions
   * @return
   */
  def fetchOptions: FetchOptions
  
  /** nothing to consume */
  def consumeContent(entity: Option[HttpEntity]) {}
  
  /** nothing to shutdown */
  protected def shutdownClient() {}
  
  /**
   * converts org.apache.http.client.methods.HttpRequestBase -> com.google.appengine.api.urlfetch.HTTPRequest
   */
  private def make_request(host: HttpHost, req: HttpRequestBase, fetchOpt: FetchOptions): HTTPRequest = {
    val uri = new java.net.URI(host.toString + req.getURI)
    val r = new HTTPRequest(uri.toURL, HTTPMethod.valueOf(req.getMethod), fetchOpt)
    req.getAllHeaders.foreach{h =>
      r.addHeader(new HTTPHeader(h.getName, h.getValue))
    }
    req match {
      case e: HttpEntityEnclosingRequestBase =>
        val payload = new java.io.ByteArrayOutputStream 
        e.getEntity.writeTo(payload)
        r.setPayload(payload.toByteArray)
      case _ =>
    }
    r
  }
  
  /**
   * suppress dispatch warning for not shutting down
   */
  override def finalize() {
    shutdown()
    super.finalize()
  }
}

/**
 * singleton async.Http with default FetchOptions
 */
object Http extends Http {
  val fetchOptions = FetchOptions.Builder.withDefaults
}

/**
 * postpone handler execution
 */
class FutureResponseWrapper[A](j: java.util.concurrent.Future[HTTPResponse], handler: HttpResponse => A, errorHandler: ExceptionListener) {
  def apply(): A = try {
    val res = j.get
    val response: HttpResponse = new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1),
                                                       res.getResponseCode,
                                                       null)
    res.getHeaders.foreach{h => 
      response.addHeader(h.getName, h.getValue)
    }
    val bae = new ByteArrayEntity(res.getContent)
    bae.setContentType(response.getFirstHeader("Content-Type"))
    response.setEntity(bae)
    
    handler(response)
  } catch {
    case e => errorHandler.lift(e); throw e
  }
  
  def isSet = j.isDone
}