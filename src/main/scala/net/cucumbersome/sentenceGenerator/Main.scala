package net.cucumbersome.sentenceGenerator

import akka.actor.{ActorSystem, Props}
import cats.data.NonEmptyVector
import cats.effect.IO
import fs2.StreamApp
import net.cucumbersome.sentenceGenerator.haikuGenerator.SyllableBasedHaikuBuilder
import net.cucumbersome.sentenceGenerator.persistence.{HaikuActor, InFileHaikuRepository}
import net.cucumbersome.sentenceGenerator.ports.web.{HaikuGeneratorWebService, HaikuPersistenceWebService}
import net.cucumbersome.sentenceGenerator.tokenizer.FromStringTokenizer
import net.cucumbersome.sentenceGenerator.wordCounter.SentenceWordCounter
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.server.middleware._

import scala.concurrent.ExecutionContext.Implicits.global
//import org.http4s.implicits._
import cats.implicits._

import scala.io.Source

object Main extends StreamApp[IO] {

  implicit val system: ActorSystem = ActorSystem()

  case class AppConfig(filePath: String)

  override def stream(args: List[String], requestShutdown: IO[Unit]): fs2.Stream[IO, StreamApp.ExitCode] = {
    val config = pureconfig.loadConfigOrThrow[AppConfig]
    val file = Source.fromFile(config.filePath, "UTF-8")
      .getLines().toList

    val sentences = FromStringTokenizer.readFromString(file.mkString("."))

    val words = SentenceWordCounter.countWords(sentences)
    val thisShouldWork = NonEmptyVector(words.head, words.tail.toVector)


    val haikuRepository = new InFileHaikuRepository(system.actorOf(Props(new HaikuActor)))
    val haikuPersistenceWebService = HaikuPersistenceWebService.service(
      haikuRepository.save,
      haikuRepository.all _
    )
    val haikuGeneratorWebService = HaikuGeneratorWebService.service(() => SyllableBasedHaikuBuilder.buildHaiku(thisShouldWork))
    /*_*/
    val httpService = CORS(haikuGeneratorWebService <+> haikuPersistenceWebService)
    /*_*/

    BlazeBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      /*_*/
      .mountService(httpService, "/")
      /*_*/
      .serve
  }
}
