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

object Main extends StreamApp[IO] {
  override def stream(args: List[String], requestShutdown: IO[Unit]): fs2.Stream[IO, StreamApp.ExitCode] = {
    val file = Source.fromFile(args.head)
      .getLines().toList


    val sentences = FromStringTokenizer.readFromString(file.mkString("\n"))
    val wordCounts = SentenceWordCounter.countWords(sentences)
    val vector = NonEmptyVector(wordCounts.head, wordCounts.toVector)

    val generator = new SimpleSentenceGenerator(new NonEmptyNextWordGenerator(vector))

    val httpService = CORS(new SimpleSentenceGeneratorWebService(generator).service)

    BlazeBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .mountService(httpService, "/")
      .serve
  }
}
