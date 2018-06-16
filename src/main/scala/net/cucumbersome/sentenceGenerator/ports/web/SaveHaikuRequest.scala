package net.cucumbersome.sentenceGenerator.ports.web

import net.cucumbersome.sentenceGenerator.domain.{Haiku, HaikuId}
import net.cucumbersome.sentenceGenerator.tokenizer.FromStringTokenizer

final case class SaveHaikuRequest(
                                   id: String,
                                   firstLine: String,
                                   middleLine: String,
                                   lastLine: String
                                 )

object SaveHaikuRequest {

  def toHaiku(saveHaikuRequest: SaveHaikuRequest): Either[Throwable, Haiku] = {
    Right(Haiku(
      id = HaikuId(saveHaikuRequest.id),
      firstLine = FromStringTokenizer.readFromString(saveHaikuRequest.firstLine).head.words,
      middleLine = FromStringTokenizer.readFromString(saveHaikuRequest.middleLine).head.words,
      lastLine = FromStringTokenizer.readFromString(saveHaikuRequest.lastLine).head.words
    ))
  }
}
