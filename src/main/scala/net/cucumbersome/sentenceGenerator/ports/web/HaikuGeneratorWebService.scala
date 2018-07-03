package net.cucumbersome.sentenceGenerator.ports.web

import cats.effect._
import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._
import net.cucumbersome.sentenceGenerator.domain.Haiku
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.impl.Root
import org.http4s.dsl.io._

import scala.language.postfixOps
object HaikuGeneratorWebService {
  def service(builder: () => IO[Haiku]): HttpService[IO] = HttpService[IO] {
    case GET -> Root / "generate-haiku" =>
      generateHaiku(builder).flatMap(Ok(_))

  }

  private def generateHaiku(builder: () => IO[Haiku]): IO[Json] = {
    builder()
      .map(HaikuResponse.fromHaiku)
      .map(_.asJson)
  }

}