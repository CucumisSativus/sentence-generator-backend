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
      "return valid if we have words with syllables from 1 to 7" in {
        val sentences = Seq(Sentence(
          Word("pies"),
          Word("pralka"),
          Word("autobus"),
          Word("magnetofon"),
          Word("pomarańczowe"),
          Word("zarchiwizowany"),
          Word("wyrewolwerowany")
        ))

        val obtained = SyllableBasedHaikuBuilder.buildSyllablesDictionary(sentences).getOrElse(throw new Exception("not valid"))

        assert(obtained.wordsBySyllablesCount(1).forall(w => WordHyphenator.hyphenateForPl(w).length == 1))
        assert(obtained.wordsBySyllablesCount(2).forall(w => WordHyphenator.hyphenateForPl(w).length == 2))
        assert(obtained.wordsBySyllablesCount(3).forall(w => WordHyphenator.hyphenateForPl(w).length == 3))
        assert(obtained.wordsBySyllablesCount(4).forall(w => WordHyphenator.hyphenateForPl(w).length == 4))
        assert(obtained.wordsBySyllablesCount(5).forall(w => WordHyphenator.hyphenateForPl(w).length == 5))
        assert(obtained.wordsBySyllablesCount(6).forall(w => WordHyphenator.hyphenateForPl(w).length == 6))
        assert(obtained.wordsBySyllablesCount(7).forall(w => WordHyphenator.hyphenateForPl(w).length == 7))
      }

      "return invalid otherwise" in {
        val sentences = Seq(Sentence())
        val obtained = SyllableBasedHaikuBuilder.buildSyllablesDictionary(sentences)
        val expected = Invalid(NonEmptyList(
          head = ValidationError(s"Zero words for 1 syllables"),
          tail = (2 to 7).map(syllablesNum =>
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
          Word("pomarańczowe"),
          Word("zarchiwizowany"),
          Word("wyrewolwerowany")
        ))

        val dict = SyllableBasedHaikuBuilder.buildSyllablesDictionary(sentences).getOrElse(throw new Exception(""))

        val haikuBuilder = new SyllableBasedHaikuBuilder(dict)

        val obtained = haikuBuilder.buildHaiku

        obtained.firstLine.map(w => WordHyphenator.hyphenateForPl(w).length).sum shouldBe 5
        obtained.middleLine.map(w => WordHyphenator.hyphenateForPl(w).length).sum shouldBe 7
        obtained.lastLine.map(w => WordHyphenator.hyphenateForPl(w).length).sum shouldBe 5
      }
    }
  }
}
