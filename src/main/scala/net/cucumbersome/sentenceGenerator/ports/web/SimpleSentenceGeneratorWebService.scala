package net.cucumbersome.sentenceGenerator.ports.web

import cats.effect._
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import net.cucumbersome.sentenceGenerator.haikuGenerator.HaikuBuilder
import net.cucumbersome.sentenceGenerator.output.toString.Shows._
import net.cucumbersome.sentenceGenerator.ports.web.SimpleSentenceGeneratorWebService.{CreateNewSentencesRequest, CreateNewSentencesResponses, GenerateHaikuResponse}
import net.cucumbersome.sentenceGenerator.sentenceGenerator.SentenceGenerator
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.impl.Root
import org.http4s.dsl.io._

class SimpleSentenceGeneratorWebService(sentenceGenerator: SentenceGenerator, haikuBuilder: HaikuBuilder) {
  private implicit val decoder: EntityDecoder[IO, CreateNewSentencesRequest] = jsonOf[IO, CreateNewSentencesRequest]
  val service: HttpService[IO] = HttpService[IO] {
    case req@POST -> Root / "generate-simple-sentence" =>
      for {
        parsed <- req.as[CreateNewSentencesRequest]
        resp <- Ok(generateSentences(parsed).asJson)
      } yield resp

    case GET -> Root / "generate-haiku" =>
      Ok(generateHaiku.asJson)
  }

  private def generateSentences(request: CreateNewSentencesRequest): CreateNewSentencesResponses = {
    val sentences = sentenceGenerator.generateSentences(request.sentenceNumber, request.sentenceMaxWordLength)
    CreateNewSentencesResponses(sentences.map(_.show))
  }

  private def generateHaiku: GenerateHaikuResponse = {
    val haiku = haikuBuilder.buildHaiku
    GenerateHaikuResponse(
      firstLine = haiku.firstLine.show,
      middleLine = haiku.middleLine.show,
      lastLine = haiku.lastLine.show
    )
  }
}

object SimpleSentenceGeneratorWebService {
  case class CreateNewSentencesRequest(sentenceMaxWordLength: Int, sentenceNumber: Int)

  case class CreateNewSentencesResponses(sentences: Seq[String])

  case class GenerateHaikuResponse(
                                  firstLine: String,
                                  middleLine: String,
                                  lastLine: String
                                  )
}