package net.cucumbersome.sentenceGenerator.haikuGenerator

import cats.data.NonEmptyList
import cats.data.Validated.Invalid
import net.cucumbersome.sentenceGenerator.domain.{Sentence, Word}
import net.cucumbersome.sentenceGenerator.haikuGenerator.SyllableBasedHaikuBuilder.ValidationError
import net.cucumbersome.sentenceGenerator.tokenizer.WordHyphenator
import org.scalatest.{Matchers, WordSpec}

class SyllableBasedHaikuBuilderTest extends WordSpec with Matchers {
  "Haiku builder" when {
    "creating new words with syllables" should {
      s"return valid if we have words with syllables from 1 to ${HaikuBuilder.haikuMaxSyllablesCount}" in {
        val sentences = Seq(Sentence(
          Word("pies"),
          Word("pralka"),
          Word("autobus"),
          Word("magnetofon"),
          Word("pomarańczowe")
        ))

        val obtained = SyllableBasedHaikuBuilder.buildSyllablesDictionary(sentences).getOrElse(throw new Exception("not valid"))

        (1 to HaikuBuilder.haikuMaxSyllablesCount).foreach { syllablesCount =>
          assert(obtained.wordsBySyllablesCount(syllablesCount).forall(w => WordHyphenator.hyphenateForPl(w).length == syllablesCount))
        }
      }

      "return invalid otherwise" in {
        val sentences = Seq(Sentence())
        val obtained = SyllableBasedHaikuBuilder.buildSyllablesDictionary(sentences)
        val expected = Invalid(NonEmptyList(
          head = ValidationError(s"Zero words for 1 syllables"),
          tail = (2 to HaikuBuilder.haikuMaxSyllablesCount).map(syllablesNum =>
            ValidationError(s"Zero words for $syllablesNum syllables")).toList)
        )

        obtained shouldBe expected
      }
    }

    "building haiku" should {
      "build it properly" in {
        val sentences = Seq(Sentence(
          Word("pies"),
          Word("pralka"),
          Word("autobus"),
          Word("magnetofon"),
          Word("pomarańczowe")
        ))

        val dict = SyllableBasedHaikuBuilder.buildSyllablesDictionary(sentences).getOrElse(throw new Exception(""))

        val haikuBuilder = new SyllableBasedHaikuBuilder(dict)

        val obtained = haikuBuilder.buildHaiku

        obtained.firstLine.map(w => WordHyphenator.hyphenateForPl(w).length).sum shouldBe 5
        obtained.middleLine.map(w => WordHyphenator.hyphenateForPl(w).length).sum shouldBe 7
        obtained.lastLine.map(w => WordHyphenator.hyphenateForPl(w).length).sum shouldBe 5
      }

      "build haiku without connectors at the end" in {
        val connectors = Seq(Word("a"), Word("z"), Word("o"))
        val sentences = Seq(Sentence(
          Word("pies"),
          Word("pralka"),
          Word("autobus"),
          Word("magnetofon"),
          Word("pomarańczowe"),
          Word("a"), Word("z"), Word("o")
        ))

        val dict = SyllableBasedHaikuBuilder.buildSyllablesDictionary(sentences).getOrElse(throw new Exception(""))

        val haikuBuilder = new SyllableBasedHaikuBuilder(dict)

        val obtained = haikuBuilder.buildHaiku

        assert(!connectors.contains(obtained.firstLine.last))
        assert(!connectors.contains(obtained.middleLine.last))
        assert(!connectors.contains(obtained.lastLine.last))
      }
    }
  }
}
