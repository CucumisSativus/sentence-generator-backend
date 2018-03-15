package net.cucumbersome.sentenceGenerator.output.toString

import cats.implicits._
import net.cucumbersome.sentenceGenerator.domain.{Sentence, Word}
import org.scalatest.{Matchers, WordSpec}

class ShowsTest extends WordSpec with Matchers {
  "Shows" should {
    "transform sentence to string" in {
      import Shows.sentenceShow
      val sentence = Sentence(Word("this"), Word("is"), Word("a"), Word("sentence"))
      sentence.show shouldBe "this is a sentence"
    }
  }

}
