package net.cucumbersome.sentenceGenerator.persistence

import akka.Done
import akka.actor._
import akka.persistence._
import net.cucumbersome.sentenceGenerator.domain.{Haiku, HaikuId}
import net.cucumbersome.sentenceGenerator.persistence.HaikuActor._

class HaikuActor(val persistenceId: String = "haiku-actor") extends PersistentActor {
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

    case RemoveHaiku(haikuId) => persist(HaikuRemoved(haikuId)) { event =>
      state = state.filter(_.id != haikuId)
      sender() ! Done
    }

    case ReadHaikus => sender() ! state
  }

}

object HaikuActor {

  sealed trait HaikuCommand

  case class SaveHaiku(haiku: Haiku) extends HaikuCommand

  case class RemoveHaiku(haikuId: HaikuId) extends HaikuCommand

  case object ReadHaikus extends HaikuCommand

  sealed trait HaikuEvent

  case class HaikuSaved(haiku: Haiku) extends HaikuEvent

  case class HaikuRemoved(haikuId: HaikuId) extends HaikuEvent
}
