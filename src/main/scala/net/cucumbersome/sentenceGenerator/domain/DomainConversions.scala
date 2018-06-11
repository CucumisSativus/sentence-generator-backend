package net.cucumbersome.sentenceGenerator.domain

trait DomainConversions {

  implicit class FromStringConverters(string: String) {
    def unsafeToWord: Word = Word(string)
  }

  implicit class FromIntConverters(integer: Int) {
    def toAppearanceCount: AppearanceCount = AppearanceCount(integer)

    def toSyllableCount: SyllableCount = SyllableCount(integer)
  }
}

object DomainConversions extends DomainConversions
