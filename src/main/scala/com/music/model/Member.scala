package com.music.model

case class Member(fullName: String,
                  aka: Option[String] = None,
                  instruments: List[String] = List.empty,
                  periods: List[Period] = List.empty)

case class Period(start: Option[Int], end: Option[Int])

object Member {
  def apply(member: String): Member = {
    val details = mapInstrumentsAndPeriods(member)

    Member(
      fullName = getFullName(member),
      aka = getAka(member),
      instruments = details("instruments"),
      periods = details("periods").map(toPeriod)
    )
  }

  private def getFullName(raw: String): String = {
    val rgxName = """([a-zA-Z\s]+)(\s\[aka\s.+\])?\s\(.+""".r
    val name = for {
      name <- rgxName.findFirstMatchIn(raw)
    } yield {
      name.group(1)
    }
    name.getOrElse("")
  }

  private def getAka(raw: String): Option[String] = {
    val rgxAka = """\[aka\s(.+)\]""".r

    for {
      aka <- rgxAka.findFirstMatchIn(raw)
    } yield {
      aka.group(1)
    }
  }

  private def mapInstrumentsAndPeriods(
      raw: String): Map[String, List[String]] = {
    val rgxDetails = """\((.+)""".r
    val details = rgxDetails.findFirstIn(raw).getOrElse("")

    val rgxInstrument = "(^[a-zA-Z\\s]+)".r
    val rgxPeriod =
      """(^[0-9]{4}$|^[0-9]{4}\-[0-9]{2,4}$|^[0-9]{4}\-present$)""".r

    def loop(input: List[String],
             output: Map[String, List[String]]): Map[String, List[String]] =
      input match {
        case Nil => output
        case remain =>
          remain.head.trim match {
            case rgxInstrument(instrument) =>
              val instruments = instrument :: output("instruments")
              loop(remain.tail, output + ("instruments" -> instruments))
            case rgxPeriod(period) =>
              val periods = period :: output("periods")
              loop(remain.tail, output + ("periods" -> periods))
            case _ => loop(remain.tail, output)
          }
      }

    loop(details.split(",").toList, Map("instruments" -> Nil, "periods" -> Nil))
  }

  private def toPeriod(raw: String): Period = {
    val rgxSingleYear = "(^[0-9]{4}$)".r
    val rgxYear4dYear = "(^[0-9]{4})\\-([0-9]{4}$)".r
    val rgxYear2dYear = "(^[0-9]{4})\\-([0-9]{2}$)".r
    val rgxYearPresent = "(^[0-9]{4})\\-present$".r

    raw match {
      case rgxSingleYear(y)    => Period(Some(y.toInt), Some(y.toInt))
      case rgxYear4dYear(s, e) => Period(Some(s.toInt), Some(e.toInt))
      case rgxYear2dYear(s, e) =>
        Period(Some(s.toInt), Some(normalizeYear(e).toInt))
      case rgxYearPresent(s) => Period(Some(s.toInt), None)
      case _                 => Period(None, None)
    }
  }

  private def normalizeYear(raw: String): String = raw.head match {
    case '0' | '1' => "20" + raw
    case _         => "19" + raw
  }
}
