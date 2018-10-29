package com.music.model

abstract class YearExtractor {
  def extractYear(raw: String): Option[Int] = {
    val rgxYear = """(\d{4})""".r

    rgxYear.findFirstIn(raw).map(_.toInt)
  }
}
