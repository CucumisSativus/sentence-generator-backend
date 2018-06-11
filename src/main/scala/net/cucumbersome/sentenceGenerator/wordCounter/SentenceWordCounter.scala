package net.cucumbersome.sentenceGenerator.wordCounter

import net.cucumbersome.sentenceGenerator.domain._
import net.cucumbersome.sentenceGenerator.tokenizer.WordHyphenator

import scala.collection.mutable.{Map => MutableMap}

object SentenceWordCounter extends DomainConversions {

  def countWords(sentences: List[Sentence]): List[WordWithSuccessors] = {
    reduceWordsWithSuccessors(sentences.flatMap(handleSentence))
  }

  private[wordCounter] def handleSentence(sentence: Sentence): List[WordWithSuccessors] = {
    def iterate(words: List[Word], acc: List[WordWithSuccessors]): List[WordWithSuccessors] = words match {
      case word :: successor :: tail => iterate(
        successor :: tail,
        acc :+ WordWithSuccessors(word, WordHyphenator.syllableCountForPl(word), Seq(Successor(successor, 1.toAppearanceCount, WordHyphenator.syllableCountForPl(successor))))
      )

      case _ => acc
    }

    iterate(sentence.words.toList, List.empty)
  }

  private[wordCounter] def reduceWordsWithSuccessors(wordsWithSuccessors: List[WordWithSuccessors]): List[WordWithSuccessors] = {
    val thisMap = MutableMap[Word, Map[Word, Int]]()
    wordsWithSuccessors.foreach { case WordWithSuccessors(word, _, successors) =>
      var successorsMapForGivenWord = thisMap.getOrElse(word, Map[Word, Int]())
      successors.foreach { successor =>
        val currentSuccCount = successorsMapForGivenWord.getOrElse(successor.successor, 0) + 1
        successorsMapForGivenWord += (successor.successor -> currentSuccCount)
      }
      thisMap += (word -> successorsMapForGivenWord)
      successorsMapForGivenWord = Map.empty
    }
    thisMap.map { case (word, successorMap) =>
      WordWithSuccessors(word, WordHyphenator.syllableCountForPl(word), successorMap.map { case (succesor, count) => Successor(succesor, count.toAppearanceCount, WordHyphenator.syllableCountForPl(succesor)) }.toSeq)
    }.toList
  }
}
