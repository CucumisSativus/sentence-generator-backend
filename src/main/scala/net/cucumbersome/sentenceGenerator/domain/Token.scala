package net.cucumbersome.sentenceGenerator.domain

final case class Word(value: String)

final case class Syllable(value: String)

final case class WordWithSyllables(
                              word: Word,
                              syllables: List[Syllable]
                            )

final case class Sentence(words: Word*) {
  def lastWord: Word = words.last
}

object Sentence {
  def fromSeq(seq: Seq[Word]): Sentence = Sentence.apply(seq: _*)


}

final case class Successor(successor: Word, count: Int)

final case class WordWithSuccessors(word: Word, successors: Seq[Successor])

final case class Haiku(
                  firstLine: Seq[Word],
                  middleLine: Seq[Word],
                  lastLine: Seq[Word]
                )
