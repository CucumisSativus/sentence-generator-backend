package net.cucumbersome.sentenceGenerator.haikuGenerator

import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, NonEmptyVector, ValidatedNel}
import cats.kernel.Semigroup
import cats.{Apply, SemigroupK}
import net.cucumbersome.sentenceGenerator.domain.{Haiku, Sentence, Word, WordWithSyllables}
import net.cucumbersome.sentenceGenerator.haikuGenerator.SyllableBasedHaikuBuilder.HaikuSyllablesDictionary
import net.cucumbersome.sentenceGenerator.tokenizer.WordHyphenator

import scala.util.Random

trait HaikuBuilder {
  def buildHaiku: Haiku
}

object HaikuBuilder {
  val haikuMaxSyllablesCount = 5
}
class SyllableBasedHaikuBuilder(syllablesDictionary: HaikuSyllablesDictionary) extends HaikuBuilder {

  private def disc = syllablesDictionary.wordsBySyllablesCount

  def buildHaiku: Haiku = {
    Haiku(
      generateLine(5),
      generateLine(7),
      generateLine(5)
    )
  }

  private def generateLine(maxSyllables: Int): Seq[Word] = {
    def iterate(syllablesLeft: Int, acc: Seq[Word]): Seq[Word] = {
      if (syllablesLeft == 0) acc
      else {
        val wordSyllableLength = 1 + Random.nextInt(Math.min(syllablesLeft, HaikuBuilder.haikuMaxSyllablesCount))
        iterate(syllablesLeft - wordSyllableLength, acc :+ getWord(wordSyllableLength))
      }
    }

    iterate(maxSyllables, Seq.empty)
  }

  private def getWord(syllables: Int) = {
    val words = disc(syllables)
    words.getUnsafe(Random.nextInt(words.length))
  }
}

object SyllableBasedHaikuBuilder {

  case class ValidationError(err: String)

  implicit val nelSemigroup: Semigroup[NonEmptyList[ValidationError]] =
    SemigroupK[NonEmptyList].algebra[ValidationError]

  class HaikuSyllablesDictionary private[haikuGenerator](val wordsBySyllablesCount: Map[Int, NonEmptyVector[Word]])



  private[haikuGenerator] def prepareWordWithSyllables(wordsWithSyllables: Seq[WordWithSyllables]): Map[Int, Seq[Word]] =
    wordsWithSyllables
      .filter(w => w.syllables.length <= HaikuBuilder.haikuMaxSyllablesCount)
      .groupBy(_.syllables.length)
      .mapValues(_.map(_.word))


  def buildSyllablesDictionary(sentences: Seq[Sentence]): ValidatedNel[ValidationError, HaikuSyllablesDictionary] = {
    val toWordWithSyllable = (w: Word) => WordWithSyllables(w, WordHyphenator.hyphenateForPl(w))
    val wordsBySyllables = prepareWordWithSyllables(sentences.flatMap(_.words).map(toWordWithSyllable))

    val validateWordsNumberBySyllables = (syllablesNum: Int) => {
      val words = wordsBySyllables.getOrElse(syllablesNum, Seq())

      words.headOption.map(head => NonEmptyVector(head, words.tail.toVector))
        .map(Valid.apply)
        .getOrElse(Invalid.apply(ValidationError(s"Zero words for $syllablesNum syllables"))).toValidatedNel
    }
    Apply[ValidatedNel[ValidationError, ?]].map5(
      validateWordsNumberBySyllables(1),
      validateWordsNumberBySyllables(2),
      validateWordsNumberBySyllables(3),
      validateWordsNumberBySyllables(4),
      validateWordsNumberBySyllables(5)
    ) { case (sylalble1, syllable2, syllable3, syllable4, syllabe5) =>
      new HaikuSyllablesDictionary(
        Map(
          1 -> sylalble1,
          2 -> syllable2,
          3 -> syllable3,
          4 -> syllable4,
          5 -> syllabe5
        )
      )
    }


  }
}
