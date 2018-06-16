package net.cucumbersome.sentenceGenerator.ports.web

import cats.effect.IO
import io.circe.generic.auto._
import org.http4s.circe._
import org.http4s.{EntityDecoder, EntityEncoder}

object JsonProtocol {

  implicit val responseDecoder: EntityDecoder[IO, HaikuResponse] = jsonOf[IO, HaikuResponse]
  implicit val responseEncoder: EntityEncoder[IO, HaikuResponse] = jsonEncoderOf[IO, HaikuResponse]
  implicit val listEncoder: EntityEncoder[IO, List[HaikuResponse]] = jsonEncoderOf[IO, List[HaikuResponse]]

  implicit val requestDecoder: EntityDecoder[IO, SaveHaikuRequest] = jsonOf[IO, SaveHaikuRequest]

}
