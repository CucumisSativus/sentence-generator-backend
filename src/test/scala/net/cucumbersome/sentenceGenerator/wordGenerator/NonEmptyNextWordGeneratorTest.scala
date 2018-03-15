package net.cucumbersome.sentenceGenerator.wordGenerator

import cats.data.NonEmptyVector
import net.cucumbersome.sentenceGenerator.domain.{Successor, Word, WordWithSuccessors}
import org.scalatest.{Matchers, WordSpec}

class NonEmptyNextWordGeneratorTest extends WordSpec with Matchers {
  "next word generator" when {
    "generating words without predecessor" should {
      "return one of the words from list if there is more than one" in {
        val words = NonEmptyVector(WordWithSuccessors(Word("word"), Seq()), Vector(WordWithSuccessors(Word("another_word"), Seq())))

        val generator = new NonEmptyNextWordGenerator(words)

        generator.nextWord should contain oneOf(Word("word"), Word("another_word"))
      }
    }

    "generating words with predecessor" should {
      "generate it from top2 predecessors" in {
        val words = NonEmptyVector(
          WordWithSuccessors(Word("word"), Seq(
            Successor(Word("succ1"), 5), Successor(Word("succ2"), 4), Successor(Word("succ3"), 3))
          ), Vector(
            WordWithSuccessors(Word("word2"), Seq(
              Successor(Word("succ4"), 5), Successor(Word("succ5"), 5), Successor(Word("succ6"), 5))
            )
          )
        )

        val generator = new NonEmptyNextWordGenerator(words)

        generator.nextWord(Word("word"), 2) should contain oneOf(Word("succ1"), Word("succ2"))
      }

      "returns none in case word has no successors" in {
        val words = NonEmptyVector.one(WordWithSuccessors(Word("word"), Seq()))

        val generator = new NonEmptyNextWordGenerator(words)

        generator.nextWord(Word("word")) shouldBe None
      }
    }
  }
}
