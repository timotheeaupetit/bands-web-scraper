package com.music.model

case class Disbanded(date: Option[Int])

object Disbanded extends YearExtractor {
  def apply(raw: String): Disbanded = {
    val year = extractYear(raw)

    Disbanded(date = year)
  }
}
