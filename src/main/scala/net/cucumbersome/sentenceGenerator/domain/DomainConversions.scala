package net.cucumbersome.sentenceGenerator.domain

trait DomainConversions {

  implicit class FromStringConverters(string: String) {
    def unsafeToWord: Word = Word(string)
  }

}

object DomainConversions extends DomainConversions
