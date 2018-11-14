package com.music.model

abstract class Milestone {
  val date: Option[Int]
}

abstract class YearExtractor {
  def extractYear(raw: String): Option[Int] = {
    val rgxYear = """(\d{4})""".r

    rgxYear.findFirstIn(raw).map(_.toInt)
  }
}

case class Formed(date: Option[Int], location: Option[Location] = None) extends Milestone

object Formed extends YearExtractor {
  def apply(raw: String): Formed = {
    val year = extractYear(raw)

    Formed(date = year)
  }
}

case class Disbanded(date: Option[Int]) extends Milestone

object Disbanded extends YearExtractor {
  def apply(raw: String): Disbanded = {
    val year = extractYear(raw)

    Disbanded(date = year)
  }
}
