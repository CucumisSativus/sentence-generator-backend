package net.cucumbersome.sentenceGenerator.ports.web

import cats.effect._
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import net.cucumbersome.sentenceGenerator.haikuGenerator.HaikuBuilder
import net.cucumbersome.sentenceGenerator.output.toString.Shows._
import net.cucumbersome.sentenceGenerator.ports.web.HaikuGeneratorWebService.GenerateHaikuResponse
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.impl.Root
import org.http4s.dsl.io._

class HaikuGeneratorWebService(haikuBuilder: HaikuBuilder) {
  val service: HttpService[IO] = HttpService[IO] {
    case GET -> Root / "generate-haiku" =>
      Ok(generateHaiku.asJson)
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

object HaikuGeneratorWebService {

  case class GenerateHaikuResponse(
                                    firstLine: String,
                                    middleLine: String,
                                    lastLine: String
                                  )

}