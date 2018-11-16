package com.music.utils

import org.specs2.Specification
import org.specs2.matcher.MatchResult
import org.specs2.specification.core.SpecStructure

class TextStripperSpec extends Specification {

  def is: SpecStructure =
    s2"""
        | A single word should just be in lower case                        $single
        | Spaces should be replaced by hyphens                              $spaces
        | Apostrophes, single or double quotes should be removed            $apostrophe
        | "&" should be replaced by the word "and"                          $ampersand
        | Accented characters should be replaced by non-accented characters $accents
        | 2-letters characters should be replaced by those 2 letters        $composed
    """.stripMargin

  private def single: MatchResult[String] =
    TextStripper.normalize("Meshuggah") must beEqualTo("meshuggah")

  private def spaces: MatchResult[String] =
    TextStripper.normalize("System Of A Down") must beEqualTo("system-of-a-down")

  private def apostrophe: MatchResult[String] =
    TextStripper.normalize("What's up") must beEqualTo("whats-up")

  private def ampersand: MatchResult[String] =
    TextStripper.normalize("Bonnie & Clyde") must beEqualTo("bonnie-and-clyde")

  private def accents: MatchResult[String] =
    TextStripper.normalize("Sólstafir") must beEqualTo("solstafir")

  private def composed: MatchResult[String] =
    TextStripper.normalize("Vænir") must beEqualTo("vaenir")

}
