package net.cucumbersome.sentenceGenerator

import cats.data.NonEmptyVector
import cats.data.Validated.{Invalid, Valid}
import cats.effect.IO
import com.typesafe.config.ConfigFactory
import fs2.StreamApp
import net.cucumbersome.sentenceGenerator.haikuGenerator.SyllableBasedHaikuBuilder
import net.cucumbersome.sentenceGenerator.ports.web.SimpleSentenceGeneratorWebService
import net.cucumbersome.sentenceGenerator.sentenceGenerator.SimpleSentenceGenerator
import net.cucumbersome.sentenceGenerator.tokenizer.FromStringTokenizer
import net.cucumbersome.sentenceGenerator.wordCounter.SentenceWordCounter
import net.cucumbersome.sentenceGenerator.wordGenerator.NonEmptyNextWordGenerator
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.server.middleware._
import pureconfig._
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


    val haikuBuilder = new SyllableBasedHaikuBuilder(haikuDictionary)

    val wordCounts = SentenceWordCounter.countWords(sentences)
    val vector = NonEmptyVector(wordCounts.head, wordCounts.toVector)

    val generator = new SimpleSentenceGenerator(new NonEmptyNextWordGenerator(vector))

    val httpService = CORS(new SimpleSentenceGeneratorWebService(generator, haikuBuilder).service)

    BlazeBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .mountService(httpService, "/")
      .serve
  }
}
