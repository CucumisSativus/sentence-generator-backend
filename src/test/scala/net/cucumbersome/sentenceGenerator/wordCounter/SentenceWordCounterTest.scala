package net.cucumbersome.sentenceGenerator.wordCounter

import net.cucumbersome.sentenceGenerator.domain._
import org.scalatest.{Matchers, WordSpec}

class SentenceWordCounterTest extends WordSpec with Matchers with DomainConversions {
  "sentence word counter" should {
    "count words from simple sentence" in {
      val sentence = Sentence(
        "dziewczyna".unsafeToWord, "kubeczek".unsafeToWord, "noga".unsafeToWord, "kubeczek".unsafeToWord, "noga".unsafeToWord
      )

      val expectedOutput = List(
        WordWithSuccessors(Word("dziewczyna"), 3.toSyllableCount, Seq(Successor(Word("kubeczek"), 1.toAppearanceCount, 3.toSyllableCount))),
        WordWithSuccessors(Word("kubeczek"), 3.toSyllableCount, Seq(Successor(Word("noga"), 2.toAppearanceCount, 2.toSyllableCount))),
        WordWithSuccessors(Word("noga"), 2.toSyllableCount, Seq(Successor(Word("kubeczek"), 1.toAppearanceCount, 3.toSyllableCount)))
      )

      SentenceWordCounter.countWords(List(sentence)) should contain theSameElementsAs expectedOutput
    }
  }

  "reduceWordsWithSuccessors" should {
    "aggregate the same words" in {
      val wordsWithSuccessors = List(
        WordWithSuccessors(Word("kwiatek"), 2.toSyllableCount, Seq(Successor(Word("kubeczek"), 1.toAppearanceCount, 3.toSyllableCount))),
        WordWithSuccessors(Word("noga"), 2.toSyllableCount, Seq(Successor(Word("kubeczek"), 1.toAppearanceCount, 3.toSyllableCount))),
        WordWithSuccessors(Word("kwiatek"), 3.toSyllableCount, Seq(Successor(Word("kubeczek"), 1.toAppearanceCount, 3.toSyllableCount))),
        WordWithSuccessors(Word("kwiatek"), 2.toSyllableCount, Seq(Successor(Word("dziewczyna"), 1.toAppearanceCount, 3.toSyllableCount)))
      )

      val expectedOutput = List(
        WordWithSuccessors(Word("noga"), 2.toSyllableCount, Seq(Successor(Word("kubeczek"), 1.toAppearanceCount, 3.toSyllableCount))),
        WordWithSuccessors(Word("kwiatek"), 2.toSyllableCount, Seq(Successor(Word("kubeczek"), 2.toAppearanceCount, 3.toSyllableCount), Successor(Word("dziewczyna"), 1.toAppearanceCount, 3.toSyllableCount)))
      )
      SentenceWordCounter.reduceWordsWithSuccessors(wordsWithSuccessors) should contain theSameElementsAs expectedOutput
    }
  }
}
