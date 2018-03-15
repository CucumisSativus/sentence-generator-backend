package net.cucumbersome.sentenceGenerator.sentenceGenerator

import net.cucumbersome.sentenceGenerator.domain.{Sentence, Word}
import net.cucumbersome.sentenceGenerator.sentenceGenerator.SentenceGeneratorTest._
import net.cucumbersome.sentenceGenerator.wordGenerator.NextWordGenerator
import org.scalatest.{Matchers, WordSpec}

class SentenceGeneratorTest extends WordSpec with Matchers {
  "sentence generator" when {
    "generating single sentence" should {
      "generate 5 words sentence if word generator allows it" in {
        val wordsGenerator = new WordGeneratorMock(5)
        val sentenceGenerator = new SentenceGenerator(wordsGenerator)

        val expectedSentence = Sentence(Word("word1"), Word("word2"), Word("word3"), Word("word4"), Word("word5"))
        sentenceGenerator.generateSentence(5) shouldBe expectedSentence
      }

      "generate sentence with as many words as word generator gives" in {
        val wordsGenerator = new WordGeneratorMock(5)
        val sentenceGenerator = new SentenceGenerator(wordsGenerator)

        val expectedSentence = Sentence(Word("word1"), Word("word2"), Word("word3"), Word("word4"), Word("word5"))
        sentenceGenerator.generateSentence(10) shouldBe expectedSentence
      }
    }
  }

}

object SentenceGeneratorTest {

  class WordGeneratorMock(maxWords: Int) extends NextWordGenerator {
    private var usedWords = 0

    override def nextWord: Option[Word] = {
      usedWords = usedWords + 1
      Some(Word(s"word$usedWords"))
    }

    override def nextWord(_previousWord: Word, selectFromTop: Int): Option[Word] = {
      if (usedWords < maxWords) nextWord
      else None
    }
  }

}
