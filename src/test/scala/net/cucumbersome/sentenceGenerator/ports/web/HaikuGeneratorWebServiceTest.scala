package net.cucumbersome.sentenceGenerator.ports.web

import io.circe.Json
import io.circe.parser._
import net.cucumbersome.sentenceGenerator.domain.{Haiku, Word}
import net.cucumbersome.sentenceGenerator.haikuGenerator.HaikuBuilder
import net.cucumbersome.sentenceGenerator.ports.web.HaikuGeneratorWebServiceTest.HaikuBuilderMock
import org.http4s._
import org.http4s.client.dsl.io._
import org.http4s.dsl.io._
import org.scalatest.{Matchers, WordSpec}

class HaikuGeneratorWebServiceTest extends WordSpec with Matchers {
  val service = new HaikuGeneratorWebService(HaikuBuilderMock)
  "simple sentence generator web service" should {
    "generate haiku" in {
      val request = GET(Uri.uri("/generate-haiku")).unsafeRunSync()
      val response = service.service.orNotFound(request).unsafeRunSync()

      response.status shouldBe Status.Ok
      val responseBody = parse(response.as[String].unsafeRunSync()).right.get

      val expectedJson = Json.obj(
        "firstLine" -> Json.fromString("first line"),
        "middleLine" -> Json.fromString("middle line"),
        "lastLine" -> Json.fromString("last line")
      )

      responseBody shouldBe expectedJson
    }
  }
}

object HaikuGeneratorWebServiceTest {

  case object HaikuBuilderMock extends HaikuBuilder {
    override def buildHaiku: Haiku = Haiku(
      Seq(Word("first"), Word("line")),
      Seq(Word("middle"), Word("line")),
      Seq(Word("last"), Word("line"))
    )
  }

}