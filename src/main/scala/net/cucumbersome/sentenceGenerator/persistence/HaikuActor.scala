package net.cucumbersome.sentenceGenerator.persistence

import akka.Done
import akka.actor._
import akka.persistence._
import net.cucumbersome.sentenceGenerator.domain.{Haiku, HaikuId}
import net.cucumbersome.sentenceGenerator.persistence.HaikuActor._

class HaikuActor(val persistenceId: String = "haiku-actor") extends PersistentActor {
  private var state = List[Haiku]()

  private def updateSteate(event: HaikuEvent) = event match {
    case HaikuSaved(haiku) => state +:= haiku
    case HaikuRemoved(haikuId) => state = state.filter(_.id != haikuId)
  }
  override def receiveRecover: Receive = {
    case event: HaikuEvent => updateSteate(event)
    case SnapshotOffer(_, snapshot: List[Haiku]) => state = snapshot
  }

  override def receiveCommand: Receive = {
    case SaveHaiku(haiku) => persist(HaikuSaved(haiku)) { event =>
      updateSteate(event)
      sender() ! Done
    }

    case RemoveHaiku(haikuId) => persist(HaikuRemoved(haikuId)) { event =>
      updateSteate(event)
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
