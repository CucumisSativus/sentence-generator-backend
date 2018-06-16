package net.cucumbersome.sentenceGenerator.haikuGenerator

import cats.data.NonEmptyVector
import net.cucumbersome.sentenceGenerator.domain.{Sentence, Word, WordWithSuccessors}
import net.cucumbersome.sentenceGenerator.wordCounter.SentenceWordCounter
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties

object HaikuBuilderProperties extends Properties("haiku") {
  val sentence: List[Sentence] = List(Sentence(
    Word("pies"),
    Word("kubek"),
    Word("pralka"),
    Word("sylwester"),
    Word("autobus"),
    Word("geografia"),
    Word("magnetofon"),
    Word("pomarańczowe"),
    Word("a"), Word("z"), Word("o"), Word("oz")
  ))

  val wordsWithSuccessors: NonEmptyVector[WordWithSuccessors] = {
    val nonValid = SentenceWordCounter.countWords(sentence)
    NonEmptyVector(nonValid.head, nonValid.tail.toVector)
  }

  property("haiku never has same words one after another") = forAll { seed: Long =>
    val haiku = SyllableBasedHaikuBuilder.buildHaiku(wordsWithSuccessors)
    nextWordDifferentFromPreviousForWholeLine(haiku.firstLine.toList) &&
      nextWordDifferentFromPreviousForWholeLine(haiku.middleLine.toList) &&
      nextWordDifferentFromPreviousForWholeLine(haiku.lastLine.toList)
  }

  def nextWordDifferentFromPreviousForWholeLine(words: List[Word]): Boolean = {
    def iterate(list: List[Word], previousWord: Word): Boolean = list match {
      case Nil => true
      case head :: _ if head == previousWord => false
      case head :: tail => iterate(tail, head)
    }

    iterate(words.tail, words.head)
  }
}
