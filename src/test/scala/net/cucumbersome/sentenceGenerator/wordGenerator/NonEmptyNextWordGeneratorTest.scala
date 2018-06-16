package net.cucumbersome.sentenceGenerator.wordGenerator

import cats.data.NonEmptyVector
import net.cucumbersome.sentenceGenerator.domain.{DomainConversions, Successor, Word, WordWithSuccessors}
import org.scalatest.{Matchers, WordSpec}

class NonEmptyNextWordGeneratorTest extends WordSpec with Matchers with DomainConversions {
  "next word generator" when {
    "generating words without predecessor" should {
      "return one of the words from list if there is more than one" in {
        val words = NonEmptyVector(WordWithSuccessors(Word("word"), 2.toSyllableCount, Seq()),
          Vector(
            WordWithSuccessors(Word("too_short"), 1.toSyllableCount, Seq()),
            WordWithSuccessors(Word("another_word"), 3.toSyllableCount, Seq()),
            WordWithSuccessors(Word("yet_another2"), 4.toSyllableCount, Seq()),
            WordWithSuccessors(Word("yet_another4"), 5.toSyllableCount, Seq())
          )
        )

        NonEmptyNextWordGenerator.firstWord(words)(3.toSyllableCount) should be(Word("another_word"))
      }
    }

    "generating words with predecessor" should {
      "generate it from top2 predecessors" in {
        val words = NonEmptyVector(
          WordWithSuccessors(Word("word"), 1.toSyllableCount, Seq(
            Successor(Word("succ1"), 1.toAppearanceCount, 5.toSyllableCount), Successor(Word("succ2"), 1.toAppearanceCount, 5.toSyllableCount), Successor(Word("succ3"), 1.toAppearanceCount, 3.toSyllableCount))
          ), Vector(
            WordWithSuccessors(Word("word2"), 1.toSyllableCount, Seq(
              Successor(Word("succ4"), 1.toAppearanceCount, 5.toSyllableCount), Successor(Word("succ5"), 1.toAppearanceCount, 5.toSyllableCount), Successor(Word("succ6"), 1.toAppearanceCount, 5.toSyllableCount))
            )
          )
        )

        NonEmptyNextWordGenerator.nextWord(words)(Word("word"), 5.toSyllableCount) should contain oneOf(Word("succ1"), Word("succ2"))
      }

      "returns none in case word has no successors" in {
        val words = NonEmptyVector.one(WordWithSuccessors(Word("word"), 1.toSyllableCount, Seq()))

        NonEmptyNextWordGenerator.nextWord(words)(Word("word"), 4.toSyllableCount) shouldBe None
      }

      "select only words with given syllable length" in {
        val words = NonEmptyVector.one(
          WordWithSuccessors(Word("word"), 1.toSyllableCount, Seq(
            Successor(Word("dziewczyna"), 5.toAppearanceCount, 3.toSyllableCount), Successor(Word("noga"), 1.toAppearanceCount, 2.toSyllableCount)
          ))
        )

        NonEmptyNextWordGenerator.nextWord(words)(Word("word"), 2.toSyllableCount) shouldBe Some(Word("noga"))
      }

      "if it cannot fulfill sylabbles needs return none" in {
        val words = NonEmptyVector.one(
          WordWithSuccessors(Word("word"), 1.toSyllableCount, Seq(
            Successor(Word("dziewczyna"), 5.toAppearanceCount, 3.toSyllableCount), Successor(Word("noga"), 1.toAppearanceCount, 2.toSyllableCount)
          ))
        )

        NonEmptyNextWordGenerator.nextWord(words)(Word("word"), 4.toSyllableCount) shouldBe None
      }
    }
  }
}
