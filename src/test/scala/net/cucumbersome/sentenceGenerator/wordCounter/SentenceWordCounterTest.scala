package net.cucumbersome.sentenceGenerator.wordCounter

import net.cucumbersome.sentenceGenerator.domain._
import org.scalatest.{Matchers, WordSpec}

class SentenceWordCounterTest extends WordSpec with Matchers with DomainConversions {
  "sentence word counter" should {
    "count words from simple sentence" in {
      val sentence = Sentence(
        "simple".unsafeToWord, "sentence".unsafeToWord, "word".unsafeToWord, "sentence".unsafeToWord, "word".unsafeToWord
      )

      val expectedOutput = List(
        WordWithSuccessors(Word("simple"), Seq(Successor(Word("sentence"), 1))),
        WordWithSuccessors(Word("sentence"), Seq(Successor(Word("word"), 2))),
        WordWithSuccessors(Word("word"), Seq(Successor(Word("sentence"), 1)))
      )

      SentenceWordCounter.countWords(List(sentence)) should contain theSameElementsAs expectedOutput
    }
  }

  "reduceWordsWithSuccessors" should {
    "aggregate the same words" in {
      val wordsWithSuccessors = List(
        WordWithSuccessors(Word("word"), Seq(Successor(Word("successor1"), 1))),
        WordWithSuccessors(Word("word2"), Seq(Successor(Word("successor1"), 1))),
        WordWithSuccessors(Word("word"), Seq(Successor(Word("successor1"), 1))),
        WordWithSuccessors(Word("word"), Seq(Successor(Word("successor2"), 1)))
      )

      val expectedOutput = List(
        WordWithSuccessors(Word("word2"), Seq(Successor(Word("successor1"), 1))),
        WordWithSuccessors(Word("word"), Seq(Successor(Word("successor1"), 2), Successor(Word("successor2"), 1)))
      )

      SentenceWordCounter.reduceWordsWithSuccessors(wordsWithSuccessors) should contain theSameElementsAs expectedOutput
    }
  }
}
