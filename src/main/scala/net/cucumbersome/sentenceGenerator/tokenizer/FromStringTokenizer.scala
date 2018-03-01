package net.cucumbersome.sentenceGenerator.tokenizer

import net.cucumbersome.sentenceGenerator.domain.{Sentence, Word}

object FromStringTokenizer {
  def readFromString(input: String): List[Sentence] = {
    val sentences = input.split('.')
    sentences.map(readSentence).filter(_.words.nonEmpty).toList
  }

  def readSentence(sentence: String): Sentence = {
    Sentence.fromSeq(
      sentence.split(' ').map(stringToWord).toSeq.filter(_.value.nonEmpty)
    )
  }

  private def stringToWord(word: String) = {
    Word(word.filter(_.isLetter).toLowerCase)
  }
}
