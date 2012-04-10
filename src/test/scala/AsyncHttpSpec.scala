package net.pomu.dispatch.gae.async

import dispatch._
import org.slim3.tester.AppEngineTester
import org.specs._

class HttpSpec extends Specification {
  val tester = new AppEngineTester
  doBeforeSpec {tester.setUp()}
  doAfterSpec {tester.tearDown()}
  
  "AsyncHttp" should {
    "get async" in {
      val req = :/("api.foursquare.com") / "v2/test" secure
      val f = Http(req as_str)
      
      f() must_== """{message: "hi"}"""
    }
  }
}