package net.cucumbersome.sentenceGenerator.wordGenerator

import cats.data.NonEmptyVector
import net.cucumbersome.sentenceGenerator.domain.{SyllableCount, Word, WordWithSuccessors}

import scala.util.Random


object NonEmptyNextWordGenerator {
  def firstWord(words: NonEmptyVector[WordWithSuccessors])(syllableCount: SyllableCount): Word = {
    val filteredWords: Vector[WordWithSuccessors] = words.filter(_.syllableCount == syllableCount)
    filteredWords(Random.nextInt(filteredWords.length)).word
  }

  def nextWord(words: NonEmptyVector[WordWithSuccessors])(previousWord: Word, syllableCount: SyllableCount, selectFromTop: Int = 5): Option[Word] = {
    words.find(_.word == previousWord)
      .map(_.successors)
      .map(_.sortWith { case (left, right) => left.count.value > right.count.value })
      .map(_.filter(_.syllableCount == syllableCount))
      .map(_.take(selectFromTop))
      .map(scala.util.Random.shuffle(_))
      .flatMap(_.headOption)
      .map(_.successor)
  }
}
