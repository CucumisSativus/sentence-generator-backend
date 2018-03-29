package net.cucumbersome.sentenceGenerator.tokenizer

import de.mfietz.jhyphenator.{HyphenationPattern, Hyphenator}
import net.cucumbersome.sentenceGenerator.domain.{Syllable, Word}
import collection.JavaConverters._

object WordHyphenator {
  private val hyphenator = Hyphenator.getInstance(HyphenationPattern.PL)
  def hyphenateForPl(word: Word): List[Syllable] = {
    hyphenator.hyphenate(word.value).asScala
      .map(Syllable.apply)
      .toList
  }
}
