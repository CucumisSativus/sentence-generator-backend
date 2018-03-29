package net.cucumbersome.sentenceGenerator.tokenizer

import net.cucumbersome.sentenceGenerator.domain.{Syllable, Word}
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{Matchers, WordSpec}

class WordHyphenatorTest extends WordSpec with Matchers with TableDrivenPropertyChecks{
  "word hyphenator" when {
    "dividing word in Polish" should {
      val testCases = Table(
        ("word", "expected"),
        ("wykształciuchy", List("wy", "kształ", "ciu", "chy")),
        ("noga", List("no", "ga")),
        ("przypadkiem", List("przy", "pad", "kiem")),
        ("dziewczyna", List("dziew", "czy", "na"))
      )


      forAll(testCases){ case(word, syllables) =>
          s"divide word '$word' into syllables" in {
            val obtained = WordHyphenator.hyphenateForPl(Word(word))
            val expected = syllables.map(Syllable.apply)

            obtained shouldBe expected
          }
      }
    }
  }
}
