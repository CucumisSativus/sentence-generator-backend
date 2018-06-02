package net.cucumbersome.sentenceGenerator.haikuGenerator

import java.util.UUID

import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, NonEmptyVector, ValidatedNel}
import cats.kernel.Semigroup
import cats.{Apply, SemigroupK}
import net.cucumbersome.sentenceGenerator.domain._
import net.cucumbersome.sentenceGenerator.tokenizer.WordHyphenator

import scala.util.Random

object HaikuBuilder {
  val haikuMaxSyllablesCount = 5
}

object SyllableBasedHaikuBuilder {
  private val connectors = Seq(
    Word("a"), Word("z"), Word("o"), Word("i"), Word("w"), Word("do"), Word("że"),
    Word("aż"), Word("do"), Word("ale"), Word("niż"), Word("dla"), Word("ależ"), Word("lecz"), Word("aby"),
    Word("ode"), Word("za"), Word("na")
  )

  def buildHaiku(haikuSyllablesDictionary: HaikuSyllablesDictionary, generateId: () => String = () => UUID.randomUUID().toString): Haiku = {
    implicit val disc: Map[Int, NonEmptyVector[Word]] = haikuSyllablesDictionary.wordsBySyllablesCount
    Haiku(
      id = HaikuId(generateId()),
      firstLine = generateLine(5),
      middleLine = generateLine(7),
      lastLine = generateLine(5)
    )
  }

  private def generateLine(maxSyllables: Int)(implicit disc: Map[Int, NonEmptyVector[Word]]): Seq[Word] = {
    def iterate(syllablesLeft: Int, acc: Seq[Word]): Seq[Word] = {
      if (syllablesLeft == 0) acc
      else {
        val wordSyllableLength = 1 + Random.nextInt(Math.min(syllablesLeft, HaikuBuilder.haikuMaxSyllablesCount))
        val generatedWord = nextWord(wordSyllableLength, syllablesLeft)

        if (acc.lastOption.contains(generatedWord)) iterate(syllablesLeft, acc)
        else iterate(syllablesLeft - wordSyllableLength, acc :+ generatedWord)
      }
    }

    iterate(maxSyllables, Seq.empty)
  }

  private def nextWord(syllableLenght: Int, syllablesLeft: Int)(implicit disc: Map[Int, NonEmptyVector[Word]]): Word = {
    if (syllableLenght == syllablesLeft) getLastWord(syllableLenght)
    else getWord(syllableLenght)
  }
  private def getWord(syllables: Int)(implicit disc: Map[Int, NonEmptyVector[Word]]) = {
    val words = disc(syllables)
    words.getUnsafe(Random.nextInt(words.length))
  }

  private def getLastWord(syllables: Int)(implicit disc: Map[Int, NonEmptyVector[Word]]): Word = {
    val words = disc(syllables).filter(w => !connectors.contains(w))
    words(Random.nextInt(words.length))
  }


  final case class ValidationError(err: String)

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
