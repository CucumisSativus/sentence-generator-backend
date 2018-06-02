package net.cucumbersome.sentenceGenerator.ports.web

import cats.effect.IO
import io.circe.Json
import io.circe.parser._
import net.cucumbersome.sentenceGenerator.domain.{Haiku, HaikuId, Word}
import org.http4s._
import org.http4s.client.dsl.io._
import org.http4s.dsl.io._
import org.scalatest.{Matchers, WordSpec}

class HaikuGeneratorWebServiceTest extends WordSpec with Matchers {
  val service: HttpService[IO] = HaikuGeneratorWebService.service(buildHaiku _)
  "simple sentence generator web service" should {
    "generate haiku" in {
      val request = GET(Uri.uri("/generate-haiku")).unsafeRunSync()
      val response = service.orNotFound(request).unsafeRunSync()

      response.status shouldBe Status.Ok
      val responseBody = parse(response.as[String].unsafeRunSync()).right.get

      val expectedJson = Json.obj(
        "id" -> Json.fromString("id"),
        "firstLine" -> Json.fromString("first line"),
        "middleLine" -> Json.fromString("middle line"),
        "lastLine" -> Json.fromString("last line")
      )

      responseBody shouldBe expectedJson
    }
  }

  def buildHaiku: Haiku = Haiku(
    id = HaikuId("id"),
    firstLine = Seq(Word("first"), Word("line")),
    middleLine = Seq(Word("middle"), Word("line")),
    lastLine = Seq(Word("last"), Word("line"))
  )
}
