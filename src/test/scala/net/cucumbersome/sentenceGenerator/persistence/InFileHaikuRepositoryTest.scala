package net.cucumbersome.sentenceGenerator.persistence

import java.io.{BufferedReader, File, FileReader}
import java.util.concurrent.atomic.AtomicInteger

import io.circe.parser._
import net.cucumbersome.sentenceGenerator.test.Fixtures
import org.scalatest.{Matchers, WordSpec}

class InFileHaikuRepositoryTest extends WordSpec with Matchers {
  "An in file haiku repository" when {
    "storing haiku" should {
      "write encoded haiku to a file" in {
        val file = getFile
        val repo = getRepo(file)
        val testHaiku = Fixtures.testHaiku

        repo.save(testHaiku).unsafeRunSync()

        val br = new BufferedReader(new FileReader(file))
        val line = br.readLine()
        val parsed = parse(line).right.get

        parsed shouldBe Fixtures.haikuAsJson
      }
    }

    "storing and decoding" should {
      "do it properly" in {
        val file = getFile
        val repo = getRepo(file)
        val testHaiku = Fixtures.testHaiku

        repo.save(testHaiku).unsafeRunSync()
        val obtained = repo.all.unsafeRunSync()

        obtained.head shouldBe testHaiku
      }
    }
  }

  def getRepo(file: File) = new InFileHaikuRepository(file)

  def getFile: File = {
    val number = new AtomicInteger()
    val fileName = s"test-file${number.getAndIncrement()}.dat"
    new File(fileName)
  }
}
