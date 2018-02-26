package net.cucumbersome.sentenceGenerator.wordCounter

import net.cucumbersome.sentenceGenerator.domain.{Sentence, Successor, Word, WordWithSuccessors}

import scala.collection.mutable.{Map => MutableMap}

object SentenceWordCounter {

  def countWords(sentences: List[Sentence]): List[WordWithSuccessors] = {
    reduceWordsWithSuccessors(sentences.flatMap(handleSentence))
  }

  private[wordCounter] def handleSentence(sentence: Sentence): List[WordWithSuccessors] = {
    def iterate(words: List[Word], acc: List[WordWithSuccessors]): List[WordWithSuccessors] = words match {
      case word :: successor :: tail => iterate(
        successor :: tail,
        acc :+ WordWithSuccessors(word, Seq(Successor(successor, 1)))
      )

      case _ => acc
    }

    iterate(sentence.words.toList, List.empty)
  }

  private[wordCounter] def reduceWordsWithSuccessors(wordsWithSuccessors: List[WordWithSuccessors]): List[WordWithSuccessors] = {
    val thisMap = MutableMap[Word, Map[Word, Int]]()
    wordsWithSuccessors.foreach { case WordWithSuccessors(word, successors) =>
      var successorsMapForGivenWord = thisMap.getOrElse(word, Map[Word, Int]())
      successors.foreach { successor =>
        val currentSuccCount = successorsMapForGivenWord.getOrElse(successor.successor, 0) + 1
        successorsMapForGivenWord += (successor.successor -> currentSuccCount)
      }
      thisMap += (word -> successorsMapForGivenWord)
      successorsMapForGivenWord = Map.empty
    }
    thisMap.map { case (word, successorMap) =>
      WordWithSuccessors(word, successorMap.map { case (succesor, count) => Successor(succesor, count) }.toSeq)
    }.toList
  }
}
