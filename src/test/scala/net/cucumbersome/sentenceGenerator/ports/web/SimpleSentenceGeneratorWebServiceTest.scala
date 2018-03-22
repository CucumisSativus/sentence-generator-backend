package net.cucumbersome.sentenceGenerator.ports.web

import io.circe.Json
import io.circe.parser._
import net.cucumbersome.sentenceGenerator.domain.{Sentence, Word}
import net.cucumbersome.sentenceGenerator.ports.web.SimpleSentenceGeneratorWebServiceTest.SentenceGeneratorMock
import net.cucumbersome.sentenceGenerator.sentenceGenerator.SentenceGenerator
import org.http4s._
import org.http4s.circe._
import org.http4s.client.dsl.io._
import org.http4s.dsl.io._
import org.scalatest.{Matchers, WordSpec}
class SimpleSentenceGeneratorWebServiceTest extends WordSpec with Matchers{
  "simple sentence generator web service" should {
    "generate sentences" in {
      val service = new SimpleSentenceGeneratorWebService(SentenceGeneratorMock)

      val requestBody =  Json.obj("sentenceMaxWordLength" -> Json.fromBigInt(1), "sentenceNumber" -> Json.fromBigInt(2))
      val request = POST(Uri.uri("/generate-simple-sentence"), requestBody).unsafeRunSync()
      val response = service.service.orNotFound(request).unsafeRunSync()

      response.status shouldBe Status.Ok
      val responseBody = parse(response.as[String].unsafeRunSync()).right.get

      val expectedJson = Json.obj("sentences" -> Json.arr(Json.fromString("word1 word2")))
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
}