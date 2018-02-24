package net.cucumbersome.sentenceGenerator.tokenizer

import net.cucumbersome.sentenceGenerator.domain.{Sentence, Word}
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{Matchers, WordSpec}

class FromStringTokenizerTest extends WordSpec with Matchers with TableDrivenPropertyChecks {
  val textAndSentences = Table(
    ("name", "sentence as string", "parsed sentence"),
    ("simple sentence", simpleSentence, expectedSimpleSentence),
    ("two sentences in one string", twoSentencesInOneString, expectedTwoSentences)
  )

  "from string tokenizer" should {
    forAll(textAndSentences) { case (name, sentenceAsString, parsedSentence) =>
      s"create tokens from $name" in {
        FromStringTokenizer.readFromString(sentenceAsString) shouldBe parsedSentence
      }
    }
  }

  def simpleSentence = "This is a simple sentence."

  def expectedSimpleSentence = List(Sentence(Word("this"), Word("is"), Word("a"), Word("simple"), Word("sentence")))

  def twoSentencesInOneString = "This is the first sentence. This is the second sentence"

  def expectedTwoSentences = List(
    Sentence(Word("this"), Word("is"), Word("the"), Word("first"), Word("sentence")),
    Sentence(Word("this"), Word("is"), Word("the"), Word("second"), Word("sentence"))
  )
}
