package net.cucumbersome.sentenceGenerator.ports.web

import cats.effect.IO
import net.cucumbersome.sentenceGenerator.domain.Haiku
import org.http4s.HttpService
import org.http4s.dsl.impl.Root
import org.http4s.dsl.io._

object HaikuPersistenceWebService {

  import JsonProtocol._

  def service(saveHaiku: Haiku => IO[Unit], readHaikus: () => IO[List[Haiku]]): HttpService[IO] = HttpService[IO] {
    case GET -> Root / "haikus" =>
      readHaikus().map(handleHaikus).flatMap(Ok(_))

    case req@POST -> Root / "save-haiku" =>
      for {
        haikuJson <- req.as[SaveHaikuRequest]
        haiku <- IO.fromEither(SaveHaikuRequest.toHaiku(haikuJson))
        _ <- saveHaiku(haiku)
        resp <- Created()
      } yield resp
  }

  private def handleHaikus(haikus: List[Haiku]) = haikus
    .map(HaikuResponse.fromHaiku)
}
