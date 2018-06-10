package net.cucumbersome.sentenceGenerator.persistence

import cats.effect.IO
import net.cucumbersome.sentenceGenerator.domain.Haiku

case class HaikuRepository(
                            save: Haiku => IO[Unit]
                          )

