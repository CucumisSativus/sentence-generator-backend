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
    Word("ode"), Word("za"), Word("na"), Word("jak"), Word("pod"), Word("ze"), Word("ta"), Word("po"), Word("nad"),
    Word("bo"), Word("czy"), Word("przy")
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

  def generateLine(maxSyllables: Int)(implicit words: NonEmptyVector[WordWithSuccessors]): Seq[Word] = {
    def iterate(syllablesCounts: List[Int], acc: NonEmptyVector[Word]): NonEmptyVector[Word] = syllablesCounts match {
      case Nil => acc
      case head :: Nil =>
        val word = generateNextWord(acc.last, head)
        if (connectors.contains(word)) iterate(syllablesCounts, acc)
        else if (word == acc.last) iterate(syllablesCounts, acc)
        else acc :+ word

      case head :: tail =>
        val word = generateNextWord(acc.last, head)
        if (word == acc.last) iterate(syllablesCounts, acc)
        else iterate(tail, acc :+ word)
    }

    val syllables = generateSyllablesNumber(maxSyllables)
    val firstWord = NonEmptyNextWordGenerator.firstWord(words)(syllables.head.toSyllableCount)
    iterate(syllables.tail, NonEmptyVector.one(firstWord)).toVector
  }

  private def generateSyllablesNumber(syllableCount: Int): List[Int] = {
    def iterate(syllablesLeft: Int, acc: List[Int]): List[Int] = {
      if (syllablesLeft == 0) acc
      else {
        val newCount = randomSyllableLength(syllablesLeft)
        iterate(syllablesLeft - newCount, acc :+ newCount)
      }
    }

    val firstSyllable = Math.min(1 + randomSyllableLength(syllableCount - 1), HaikuBuilder.haikuMaxSyllablesCount)
    iterate(syllableCount - firstSyllable, List(firstSyllable)).reverse
  }

  private def randomSyllableLength(syllablesLeft: Int): Int =
    1 + Random.nextInt(Math.min(syllablesLeft, HaikuBuilder.haikuMaxSyllablesCount))

  private def generateNextWord(previousWord: Word, syllableLenght: Int)(implicit words: NonEmptyVector[WordWithSuccessors]): Word = {
    NonEmptyNextWordGenerator.nextWord(words)(previousWord, syllableLenght.toSyllableCount)
      .getOrElse(NonEmptyNextWordGenerator.firstWord(words)(syllableLenght.toSyllableCount))
  }
}
