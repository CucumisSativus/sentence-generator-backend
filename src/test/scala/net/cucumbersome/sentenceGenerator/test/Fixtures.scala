package net.cucumbersome.sentenceGenerator.test

import io.circe.Json
import net.cucumbersome.sentenceGenerator.domain.{Haiku, HaikuId, Word}

object Fixtures {
  val haikuAsJson: Json = Json.obj(
    "id" -> Json.fromString("id"),
    "firstLine" -> Json.fromString("first line"),
    "middleLine" -> Json.fromString("middle line"),
    "lastLine" -> Json.fromString("last line")
  )

  val testHaiku: Haiku = Haiku(
    id = HaikuId("id"),
    firstLine = Seq(Word("first"), Word("line")),
    middleLine = Seq(Word("middle"), Word("line")),
    lastLine = Seq(Word("last"), Word("line"))
  )
}
