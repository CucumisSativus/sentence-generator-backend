package net.cucumbersome.sentenceGenerator.test

import java.util.UUID

import net.cucumbersome.sentenceGenerator.domain.{DomainConversions, Haiku, HaikuId, Word}

object Fixtures extends DomainConversions {

  def haiku: Haiku = Haiku(
    id = HaikuId(UUID.randomUUID().toString),
    firstLine = Seq(Word("zbierającego")),
    middleLine = Seq(Word("zbierającego"), Word("bólu")),
    lastLine = Seq(Word("zimowe"), Word("wszystkich"))
  )
}
