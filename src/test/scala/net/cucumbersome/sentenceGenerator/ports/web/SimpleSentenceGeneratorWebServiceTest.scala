package net.cucumbersome.sentenceGenerator.ports.web

import io.circe.Json
import io.circe.parser._
import net.cucumbersome.sentenceGenerator.domain.{Haiku, Sentence, Word}
import net.cucumbersome.sentenceGenerator.haikuGenerator.HaikuBuilder
import net.cucumbersome.sentenceGenerator.ports.web.SimpleSentenceGeneratorWebServiceTest.{HaikuBuilderMock, SentenceGeneratorMock}
import net.cucumbersome.sentenceGenerator.sentenceGenerator.SentenceGenerator
import org.http4s._
import org.http4s.circe._
import org.http4s.client.dsl.io._
import org.http4s.dsl.io._
import org.scalatest.{Matchers, WordSpec}
class SimpleSentenceGeneratorWebServiceTest extends WordSpec with Matchers{
  "simple sentence generator web service" should {
    val service = new SimpleSentenceGeneratorWebService(SentenceGeneratorMock, HaikuBuilderMock)
    "generate sentences" in {
      val requestBody =  Json.obj("sentenceMaxWordLength" -> Json.fromBigInt(1), "sentenceNumber" -> Json.fromBigInt(2))
      val request = POST(Uri.uri("/generate-simple-sentence"), requestBody).unsafeRunSync()
      val response = service.service.orNotFound(request).unsafeRunSync()

      response.status shouldBe Status.Ok
      val responseBody = parse(response.as[String].unsafeRunSync()).right.get

      val expectedJson = Json.obj("sentences" -> Json.arr(Json.fromString("word1 word2")))
      responseBody shouldBe expectedJson
    }

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

object SimpleSentenceGeneratorWebServiceTest{
  case object SentenceGeneratorMock extends SentenceGenerator{
    override def generateSentences(number: Int, maxSentenceLength: Int): Seq[Sentence] = {
      Seq(
        Sentence(Word("word1"), Word("word2"))
      )
    }
  }

  case object HaikuBuilderMock extends HaikuBuilder{
    override def buildHaiku: Haiku = Haiku(
      Seq(Word("first"), Word("line")),
      Seq(Word("middle"), Word("line")),
      Seq(Word("last"), Word("line"))
    )
  }

}