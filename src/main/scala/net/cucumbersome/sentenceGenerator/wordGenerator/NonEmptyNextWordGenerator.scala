package net.cucumbersome.sentenceGenerator.wordGenerator

import cats.data.NonEmptyVector
import net.cucumbersome.sentenceGenerator.domain.{Word, WordWithSuccessors}

import scala.util.Random

trait NextWordGenerator {
  def nextWord: Option[Word]

  def nextWord(previousWord: Word, selectFromTop: Int = 5): Option[Word]
}

class NonEmptyNextWordGenerator(words: NonEmptyVector[WordWithSuccessors]) extends NextWordGenerator {
  def nextWord: Option[Word] = {
    words.get(Random.nextInt(words.length)).map(_.word)
  }

  def nextWord(previousWord: Word, selectFromTop: Int = 5): Option[Word] = {
    words.find(_.word == previousWord)
      .map(_.successors)
      .map(_.sortWith{ case(left, right) => left.count > right.count})
      .map(_.take(selectFromTop))
      .map(scala.util.Random.shuffle(_))
      .flatMap(_.headOption)
      .map(_.successor)
  }
}
