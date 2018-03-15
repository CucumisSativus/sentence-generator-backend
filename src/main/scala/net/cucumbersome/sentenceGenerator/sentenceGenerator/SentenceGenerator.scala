package net.cucumbersome.sentenceGenerator.sentenceGenerator

import net.cucumbersome.sentenceGenerator.domain.{Sentence, Word}
import net.cucumbersome.sentenceGenerator.wordGenerator.NextWordGenerator

import scala.annotation.tailrec

class SentenceGenerator(generator: NextWordGenerator) {

  def generateSentence(length: Int): Sentence = {

    @tailrec
    def iterate(words: Seq[Word], count: Int): Seq[Word] = {
      generator.nextWord(words.last) match {
        case Some(word) if count < length => iterate(words :+ word, count + 1)
        case _ => words
      }
    }

    val words =
      generator.nextWord
        .map(word => iterate(Seq(word), 0))
        .getOrElse(Seq.empty)
    Sentence(words: _*)
  }
}

