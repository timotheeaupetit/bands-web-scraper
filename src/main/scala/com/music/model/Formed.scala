package com.music.model

case class Formed(date: Option[Int], location: Option[Location] = None)

object Formed extends YearExtractor {
  def apply(raw: String): Formed = {
    val year = extractYear(raw)

    Formed(date = year)
  }
}
