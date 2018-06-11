package net.cucumbersome.sentenceGenerator.tokenizer

import de.mfietz.jhyphenator.{HyphenationPattern, Hyphenator}
import net.cucumbersome.sentenceGenerator.domain.{Syllable, SyllableCount, Word}

import scala.collection.JavaConverters._

object WordHyphenator {
  private val hyphenator = Hyphenator.getInstance(HyphenationPattern.PL)
  def hyphenateForPl(word: Word): List[Syllable] = {
    hyphenator.hyphenate(word.value).asScala
      .map(Syllable.apply)
      .toList
  }

  def syllableCountForPl(word: Word): SyllableCount = SyllableCount(hyphenateForPl(word).length)
}
