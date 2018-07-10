package net.cucumbersome.sentenceGenerator.persistence

import java.util.UUID

import akka.actor.{ActorSystem, Props}
import net.cucumbersome.sentenceGenerator.domain.{HaikuId, Word}
import net.cucumbersome.sentenceGenerator.test.Fixtures
import org.scalatest.{Matchers, WordSpec}


class InFileHaikuRepositoryTest extends WordSpec with Matchers {
  private implicit val system: ActorSystem = ActorSystem("test")
  "An in file haiku repository" when {
    "storing and decoding" should {
      "do it properly" in {
        val repo = getRepo
        val testHaiku1 = Fixtures.testHaiku
        val testHaiku2 = Fixtures.testHaiku.copy(id = HaikuId("other"), lastLine = Seq(Word("word")))

        repo.save(testHaiku1).unsafeRunSync()
        repo.save(testHaiku2).unsafeRunSync()

        val obtained = repo.all.unsafeRunSync()

        obtained should contain theSameElementsAs Seq(testHaiku1, testHaiku2)
      }
    }

    "removing haikus" should {
      "do it properly" in {
        val repo = getRepo
        val testHaiku1 = Fixtures.testHaiku
        val testHaiku2 = Fixtures.testHaiku.copy(id = HaikuId("other"), lastLine = Seq(Word("word")))

        repo.save(testHaiku1).unsafeRunSync()
        repo.save(testHaiku2).unsafeRunSync()
        repo.remove(testHaiku2.id).unsafeRunSync()

        val obtained = repo.all.unsafeRunSync()

        obtained should contain theSameElementsAs Seq(testHaiku1)
      }
    }
  }

  def getRepo = new InFileHaikuRepository(system.actorOf(Props(new HaikuActor(UUID.randomUUID().toString))))
}
