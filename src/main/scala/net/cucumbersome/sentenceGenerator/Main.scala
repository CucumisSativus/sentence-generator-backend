package net.cucumbersome.sentenceGenerator

import cats.data.NonEmptyVector
import net.cucumbersome.sentenceGenerator.tokenizer.FromStringTokenizer
import net.cucumbersome.sentenceGenerator.wordCounter.SentenceWordCounter
import net.cucumbersome.sentenceGenerator.wordGenerator.NonEmptyNextWordGenerator

import scala.io.Source
object Main {
  def main(args: Array[String]): Unit = {
    val file = Source.fromFile("/Users/michal/Documents/teksty.txt")
      .getLines().toList


    val sentences = FromStringTokenizer.readFromString(file.mkString("\n"))
    val wordCounts = SentenceWordCounter.countWords(sentences)
    val vector = NonEmptyVector(wordCounts.head, wordCounts.toVector)

    val generator = new NonEmptyNextWordGenerator(vector)

    for (x <- 0 until 20) {
      print(x)
      //      println(generateSentence(generator, 8))
      println()
    }
  }


}
