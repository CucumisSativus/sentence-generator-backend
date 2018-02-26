package net.cucumbersome.sentenceGenerator.domain

case class Word(value: String)

case class Sentence(words: Word*)

object Sentence {
  def fromSeq(seq: Seq[Word]): Sentence = Sentence.apply(seq: _*)
}

case class Successor(successor: Word, count: Int)

case class WordWithSuccessors(word: Word, successors: Seq[Successor])

