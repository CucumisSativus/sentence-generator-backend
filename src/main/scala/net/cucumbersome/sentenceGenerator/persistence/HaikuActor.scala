package net.cucumbersome.sentenceGenerator.persistence

import akka.Done
import akka.actor._
import akka.persistence._
import net.cucumbersome.sentenceGenerator.domain.Haiku
import net.cucumbersome.sentenceGenerator.persistence.HaikuActor.{HaikuSaved, ReadHaikus, SaveHaiku}

class HaikuActor extends PersistentActor {
  private var state = List[Haiku]()

  override def receiveRecover: Receive = {
    case HaikuSaved(haiku) => state +:= haiku
    case SnapshotOffer(_, snapshot: List[Haiku]) => state = snapshot
  }

  override def receiveCommand: Receive = {
    case SaveHaiku(haiku) => persist(HaikuSaved(haiku)) { event =>
      state +:= event.haiku
      sender() ! Done
    }

    case ReadHaikus => sender() ! state
  }

  override def persistenceId: String = "haiku-actor"
}

object HaikuActor {

  sealed trait HaikuCommand

  case class SaveHaiku(haiku: Haiku) extends HaikuCommand

  case object ReadHaikus extends HaikuCommand

  sealed trait HaikuEvent

  case class HaikuSaved(haiku: Haiku) extends HaikuEvent

}
