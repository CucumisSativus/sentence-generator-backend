package net.cucumbersome.sentenceGenerator.ports.web

import cats.implicits._
import net.cucumbersome.sentenceGenerator.domain.Haiku
import net.cucumbersome.sentenceGenerator.output.toString.Shows._

final case class HaikuResponse(
                                id: String,
                                firstLine: String,
                                middleLine: String,
                                lastLine: String
                              )

object HaikuResponse {
  def fromHaiku(haiku: Haiku): HaikuResponse = HaikuResponse(
    id = haiku.id.show,
    firstLine = haiku.firstLine.show,
    middleLine = haiku.middleLine.show,
    lastLine = haiku.lastLine.show
  )
}