package net.cucumbersome.sentenceGenerator.ports.web

import cats.effect._
import cats.implicits._
import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._
import net.cucumbersome.sentenceGenerator.domain.Haiku
import net.cucumbersome.sentenceGenerator.output.toString.Shows._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.impl.Root
import org.http4s.dsl.io._
import scala.concurrent.duration._
import scala.language.postfixOps
object HaikuGeneratorWebService {
  def service(builder: () => Haiku): HttpService[IO] = HttpService[IO] {
    case GET -> Root / "generate-haiku" =>
      generateHaiku(builder).unsafeRunTimed(3 seconds) match {
        case Some(res) => Ok(res)
        case None => InternalServerError()
      }
  }

  private def generateHaiku(builder: () => Haiku): IO[Json] = {
    IO(builder()).map(haiku =>
      GenerateHaikuResponse(
        id = haiku.id.show,
        firstLine = haiku.firstLine.show,
        middleLine = haiku.middleLine.show,
        lastLine = haiku.lastLine.show
      )
    ).map(_.asJson)
  }


  final case class GenerateHaikuResponse(
                                          id: String,
                                          firstLine: String,
                                          middleLine: String,
                                          lastLine: String
                                        )

}