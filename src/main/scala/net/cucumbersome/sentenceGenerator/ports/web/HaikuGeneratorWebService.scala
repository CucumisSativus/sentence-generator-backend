package net.cucumbersome.sentenceGenerator.ports.web

import cats.effect._
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import net.cucumbersome.sentenceGenerator.domain.Haiku
import net.cucumbersome.sentenceGenerator.output.toString.Shows._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.impl.Root
import org.http4s.dsl.io._

object HaikuGeneratorWebService {
  def service(builder: () => Haiku): HttpService[IO] = HttpService[IO] {
    case GET -> Root / "generate-haiku" =>
      Ok(generateHaiku(builder).asJson)
  }

  private def generateHaiku(builder: () => Haiku): GenerateHaikuResponse = {
    val haiku = builder()
    GenerateHaikuResponse(
      id = haiku.id.show,
      firstLine = haiku.firstLine.show,
      middleLine = haiku.middleLine.show,
      lastLine = haiku.lastLine.show
    )
  }


  final case class GenerateHaikuResponse(
                                          id: String,
                                          firstLine: String,
                                          middleLine: String,
                                          lastLine: String
                                  )

}