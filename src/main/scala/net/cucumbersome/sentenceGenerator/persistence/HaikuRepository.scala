package net.cucumbersome.sentenceGenerator.persistence

import akka.actor._
import akka.pattern._
import akka.util.Timeout
import cats.effect._
import net.cucumbersome.sentenceGenerator.domain.{Haiku, HaikuId}
import net.cucumbersome.sentenceGenerator.persistence.HaikuActor._

import scala.concurrent.duration._
import scala.language.postfixOps

trait HaikuRepository {
  def save(haiku: Haiku): IO[Unit]

  def all: IO[List[Haiku]]

  def remove(haikuId: HaikuId): IO[Unit]
}


class InFileHaikuRepository(actorRef: ActorRef) extends HaikuRepository {
  private implicit val timeout: Timeout = Timeout(1 second)

  override def save(haiku: Haiku): IO[Unit] = {
    IO.fromFuture(
      IO(
        actorRef ? SaveHaiku(haiku)
      )
    ).map(_ => ())
  }


  override def all: IO[List[Haiku]] = {
    IO.fromFuture(
      IO(
        (actorRef ? ReadHaikus).mapTo[List[Haiku]]
      )
    )
  }

  override def remove(haikuId: HaikuId): IO[Unit] = {
    IO.fromFuture(
      IO(
        (actorRef ? RemoveHaiku(haikuId))
      )
    ).map(_ => ())
  }
}

