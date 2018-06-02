package net.cucumbersome.sentenceGenerator.domain

final case class Word(value: String) extends AnyVal

final case class Syllable(value: String) extends AnyVal

final case class WordWithSyllables(
                                    word: Word,
                                    syllables: List[Syllable]
                                  )

final case class Sentence(words: Word*)

object Sentence {
  def fromSeq(seq: Seq[Word]): Sentence = Sentence.apply(seq: _*)


}

final case class Successor(successor: Word, count: Int)

final case class WordWithSuccessors(word: Word, successors: Seq[Successor])

final case class HaikuId(value: String) extends AnyVal

final case class Haiku(
                        id: HaikuId,
                        firstLine: Seq[Word],
                        middleLine: Seq[Word],
                        lastLine: Seq[Word]
                      )
