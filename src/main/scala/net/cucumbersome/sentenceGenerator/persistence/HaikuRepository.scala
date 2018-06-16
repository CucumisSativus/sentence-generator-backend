package net.cucumbersome.sentenceGenerator.persistence

import cats.effect.IO
import net.cucumbersome.sentenceGenerator.domain.Haiku

trait HaikuRepository {
  def save(haiku: Haiku): IO[Unit]

  def all: IO[List[Haiku]]
}

class InMemoryHaikuRepostiory extends HaikuRepository {
  var haikus: List[Haiku] = List.empty

  override def save(haiku: Haiku): IO[Unit] = {
    haikus = haikus :+ haiku
    IO.unit
  }

  override def all: IO[List[Haiku]] = {
    IO.pure(haikus)
  }
}
