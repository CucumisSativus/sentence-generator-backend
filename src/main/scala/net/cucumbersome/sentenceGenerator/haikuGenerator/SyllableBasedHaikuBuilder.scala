package net.cucumbersome.sentenceGenerator.haikuGenerator

import java.util.UUID

import cats.data.NonEmptyVector
import net.cucumbersome.sentenceGenerator.domain._
import net.cucumbersome.sentenceGenerator.wordGenerator.NonEmptyNextWordGenerator

import scala.util.Random

object HaikuBuilder {
  val haikuMaxSyllablesCount = 5
}

object SyllableBasedHaikuBuilder extends DomainConversions {
  private val connectors = Seq(
    Word("a"), Word("z"), Word("o"), Word("i"), Word("w"), Word("do"), Word("że"),
    Word("aż"), Word("do"), Word("ale"), Word("niż"), Word("dla"), Word("ależ"), Word("lecz"), Word("aby"),
    Word("ode"), Word("za"), Word("na")
  )

  def buildHaiku(haikuSyllablesDictionary: NonEmptyVector[WordWithSuccessors], generateId: () => String = () => UUID.randomUUID().toString): Haiku = {
    implicit val dict: NonEmptyVector[WordWithSuccessors] = haikuSyllablesDictionary
    Haiku(
      id = HaikuId(generateId()),
      firstLine = generateLine(5),
      middleLine = generateLine(7),
      lastLine = generateLine(5)
    )
  }

  private def generateLine(maxSyllables: Int)(implicit words: NonEmptyVector[WordWithSuccessors]): Seq[Word] = {
    def iterate(syllablesLeft: Int, acc: NonEmptyVector[Word]): NonEmptyVector[Word] = {
      if (syllablesLeft == 0) acc
      else {
        val wordSyllableLength = randomSyllableLength(syllablesLeft)
        val generatedWord = generateNextWord(acc.last, wordSyllableLength)

        if (acc.find(_ == generatedWord).isDefined) iterate(syllablesLeft, acc)
        else if (connectors.contains(generatedWord)) iterate(syllablesLeft, acc)
        else iterate(syllablesLeft - wordSyllableLength, acc :+ generatedWord)
      }
    }

    val firstWordSyllableCount = randomSyllableLength(maxSyllables)
    val firstWord = NonEmptyNextWordGenerator.firstWord(words)(firstWordSyllableCount.toSyllableCount)
    iterate(maxSyllables - firstWordSyllableCount, NonEmptyVector.one(firstWord)).toVector
  }

  private def randomSyllableLength(syllablesLeft: Int): Int =
    1 + Random.nextInt(Math.min(syllablesLeft, HaikuBuilder.haikuMaxSyllablesCount))

  private def generateNextWord(previousWord: Word, syllableLenght: Int)(implicit words: NonEmptyVector[WordWithSuccessors]): Word = {
    NonEmptyNextWordGenerator.nextWord(words)(previousWord, syllableLenght.toSyllableCount)
      .getOrElse(NonEmptyNextWordGenerator.firstWord(words)(syllableLenght.toSyllableCount))
  }
}
