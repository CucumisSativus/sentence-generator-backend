package net.cucumbersome.sentenceGenerator.wordGenerator

import cats.data.NonEmptyVector
import net.cucumbersome.sentenceGenerator.domain.{Word, WordWithSuccessors}

import scala.util.Random


object NonEmptyNextWordGenerator {
  def firstWord(words: NonEmptyVector[WordWithSuccessors]): Option[Word] = {
    words.get(Random.nextInt(words.length)).map(_.word)
  }

  def nextWord(words: NonEmptyVector[WordWithSuccessors])(previousWord: Word, selectFromTop: Int = 5): Option[Word] = {
    words.find(_.word == previousWord)
      .map(_.successors)
      .map(_.sortWith{ case(left, right) => left.count > right.count})
      .map(_.take(selectFromTop))
      .map(scala.util.Random.shuffle(_))
      .flatMap(_.headOption)
      .map(_.successor)
  }
}
