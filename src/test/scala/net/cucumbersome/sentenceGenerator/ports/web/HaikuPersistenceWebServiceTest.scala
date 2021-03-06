package net.cucumbersome.sentenceGenerator.ports.web

import cats.effect.IO
import io.circe.Json
import io.circe.parser._
import net.cucumbersome.sentenceGenerator.domain.{Haiku, HaikuId}
import net.cucumbersome.sentenceGenerator.test.Fixtures
import org.http4s._
import org.http4s.client.dsl.io._
import org.http4s.dsl.io._
import org.scalatest.{Matchers, WordSpec}

class HaikuPersistenceWebServiceTest extends WordSpec with Matchers {


  "Haiku persistence web service" when {
    "saving new haiku" should {
      val getHaikus = () => IO.pure(List.empty[Haiku])
      val removeHaikus = (_: HaikuId) => IO.unit
      "save a proper haiku" in {
        var obtainedHaiku: Option[Haiku] = None
        val saveHaiku = (haiku: Haiku) => {
          obtainedHaiku = Some(haiku)
          IO.unit
        }

        val service = HaikuPersistenceWebService.service(saveHaiku, getHaikus, removeHaikus, "")

        val requestBody = Fixtures.haikuAsJson
        val request = POST(Uri.uri("/save-haiku"), requestBody.noSpaces).unsafeRunSync()
        val response = service.orNotFound(request).unsafeRunSync()

        response.status shouldBe Status.Created
        obtainedHaiku shouldBe Some(Fixtures.testHaiku)
      }
    }

    "reading haikus from the database" should {
      val saveHaiku = (haiku: Haiku) => IO.unit
      val getHaikus = () => IO.pure(List(Fixtures.testHaiku))
      val removeHaikus = (_: HaikuId) => IO.unit
      val service = HaikuPersistenceWebService.service(saveHaiku, getHaikus, removeHaikus, "")
      "return list of haikus" in {

        val request = GET(Uri.uri("/haikus")).unsafeRunSync()
        val response = service.orNotFound(request).unsafeRunSync()

        response.status shouldBe Status.Ok
        val responseBOdy = parse(response.as[String].unsafeRunSync()).right.get

        val expectedJson = Json.arr(Fixtures.haikuAsJson)
        responseBOdy shouldBe expectedJson
      }
    }

    "removing haikus from the database" should {
      val saveHaiku = (haiku: Haiku) => IO.unit
      val getHaikus = () => IO.pure(List.empty[Haiku])

      "return 401 if password is incorrect" in {
        var wasCalledWithId: Option[HaikuId] = None
        val removeHaikus = (a: HaikuId) => IO {
          wasCalledWithId = Some(a)
          ()
        }

        val service = HaikuPersistenceWebService.service(saveHaiku, getHaikus, removeHaikus, "right")
        val request = DELETE(Uri.uri("/delete-haiku/haiku-id/wrong")).unsafeRunSync()
        val response = service.orNotFound(request).unsafeRunSync()

        response.status shouldBe Status.Forbidden
        wasCalledWithId shouldBe None
      }

      "remove haiku if proper password is supplied" in {
        var wasCalledWithId: Option[HaikuId] = None
        val removeHaikus = (a: HaikuId) => IO {
          wasCalledWithId = Some(a)
          ()
        }

        val service = HaikuPersistenceWebService.service(saveHaiku, getHaikus, removeHaikus, "right")
        val request = DELETE(Uri.uri("/delete-haiku/haiku-id/right")).unsafeRunSync()
        val response = service.orNotFound(request).unsafeRunSync()

        response.status shouldBe Status.Ok
        wasCalledWithId shouldBe Some(HaikuId("haiku-id"))
      }
    }
  }

}
