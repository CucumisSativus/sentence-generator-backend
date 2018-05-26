package net.cucumbersome.sentenceGenerator

import cats.data.Validated.{Invalid, Valid}
import cats.effect.IO
import fs2.StreamApp
import net.cucumbersome.sentenceGenerator.haikuGenerator.SyllableBasedHaikuBuilder
import net.cucumbersome.sentenceGenerator.ports.web.HaikuGeneratorWebService
import net.cucumbersome.sentenceGenerator.tokenizer.FromStringTokenizer
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.server.middleware._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source

object Main extends StreamApp[IO] {
  case class AppConfig(filePath: String)
  override def stream(args: List[String], requestShutdown: IO[Unit]): fs2.Stream[IO, StreamApp.ExitCode] = {
    val config = pureconfig.loadConfigOrThrow[AppConfig]
    val file = Source.fromFile(config.filePath, "UTF-8")
      .getLines().toList

    val sentences = FromStringTokenizer.readFromString(file.mkString("."))

    val haikuDictionary = SyllableBasedHaikuBuilder.buildSyllablesDictionary(sentences) match {
      case Valid(d) => d
      case Invalid(errors) => throw new Exception(errors.toList.mkString(" "))
    }


    val httpService = CORS(HaikuGeneratorWebService.service(() => SyllableBasedHaikuBuilder.buildHaiku(haikuDictionary)))

    BlazeBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .mountService(httpService, "/")
      .serve
  }
}
