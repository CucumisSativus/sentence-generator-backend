package net.cucumbersome.sentenceGenerator.haikuGenerator

import cats.data.NonEmptyVector
import net.cucumbersome.sentenceGenerator.domain.{Sentence, Word, WordWithSuccessors}
import net.cucumbersome.sentenceGenerator.tokenizer.WordHyphenator
import net.cucumbersome.sentenceGenerator.wordCounter.SentenceWordCounter
import org.scalatest.{Matchers, WordSpec}

class SyllableBasedHaikuBuilderTest extends WordSpec with Matchers {
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
  "Haiku builder" when {
    "building haiku" should {
      "build it properly" in {
        val obtained = SyllableBasedHaikuBuilder.buildHaiku(wordsWithSuccessors).unsafeRunSync()

        obtained.firstLine.map(w => WordHyphenator.hyphenateForPl(w).length).sum shouldBe 5
        obtained.middleLine.map(w => WordHyphenator.hyphenateForPl(w).length).sum shouldBe 7
        obtained.lastLine.map(w => WordHyphenator.hyphenateForPl(w).length).sum shouldBe 5
      }

      "build haiku without connectors at the end" in {
        val connectors = Seq(Word("a"), Word("z"), Word("o"))

        val obtained = SyllableBasedHaikuBuilder.buildHaiku(wordsWithSuccessors).unsafeRunSync()

        assert(!connectors.contains(obtained.firstLine.last))
        assert(!connectors.contains(obtained.middleLine.last))
        assert(!connectors.contains(obtained.lastLine.last))
      }
    }
  }
}
