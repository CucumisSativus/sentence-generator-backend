package net.cucumbersome.sentenceGenerator

import cats.data.NonEmptyVector
import cats.effect.IO
import fs2.StreamApp
import net.cucumbersome.sentenceGenerator.ports.web.SimpleSentenceGeneratorWebService
import net.cucumbersome.sentenceGenerator.sentenceGenerator.SimpleSentenceGenerator
import net.cucumbersome.sentenceGenerator.tokenizer.FromStringTokenizer
import net.cucumbersome.sentenceGenerator.wordCounter.SentenceWordCounter
import net.cucumbersome.sentenceGenerator.wordGenerator.NonEmptyNextWordGenerator
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.server.middleware._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source
import scala.concurrent.duration._
object Main extends StreamApp[IO] {

  private val file = Source.fromFile("/Users/michal/Documents/teksty.txt")
    .getLines().toList


  private val sentences = FromStringTokenizer.readFromString(file.mkString("\n"))
  private val wordCounts = SentenceWordCounter.countWords(sentences)
  private val vector = NonEmptyVector(wordCounts.head, wordCounts.toVector)

  private val generator = new SimpleSentenceGenerator(new NonEmptyNextWordGenerator(vector))

  private val httpService = CORS(new SimpleSentenceGeneratorWebService(generator).service)


  override def stream(args: List[String], requestShutdown: IO[Unit]): fs2.Stream[IO, StreamApp.ExitCode] = {
    BlazeBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .mountService(httpService, "/")
      .serve
  }
}
