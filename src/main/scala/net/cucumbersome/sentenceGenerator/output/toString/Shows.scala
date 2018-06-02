package net.cucumbersome.sentenceGenerator.output.toString

import cats._
import net.cucumbersome.sentenceGenerator.domain.{HaikuId, Sentence, Word}

object Shows {
  implicit val sentenceShow: Show[Sentence] = Show.show { sentence =>
    sentence
      .words
      .map(_.value)
      .mkString(" ")
  }

  implicit val wordsShow: Show[Seq[Word]] = Show.show { words =>
    words
      .map(_.value)
      .mkString(" ")
  }

  implicit val idShow: Show[HaikuId] = Show.show { id => id.value }
}
