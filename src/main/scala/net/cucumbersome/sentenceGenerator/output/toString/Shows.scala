package net.cucumbersome.sentenceGenerator.output.toString

import cats._
import net.cucumbersome.sentenceGenerator.domain.Sentence

object Shows {
  implicit val sentenceShow: Show[Sentence] = Show.show { sentence =>
    sentence
      .words
      .map(_.value)
      .mkString(" ")
  }
}
