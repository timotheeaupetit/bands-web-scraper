package com.music

import com.music.model.BandPage
import com.music.utils.{Resources, TextStripper}

case class Processor(baseUrl: String) {
  def process(): Unit = {
    println("*** Processing ***")
    Resources.BANDS.foreach(processOne)
  }

  private def processOne(band: String): Unit = {
    println(band)
    val potentialUrls = potentialNames(band).map(guessUrl).toList

    def tryUrls(current: String, remaining: List[String]): Unit = {
      Scraper(current) match {
        case Some(scraper) =>
          println(current)
          val bandPage = scraper.buildObject
          sendData(bandPage)
          Thread.sleep(4000)
        case _             =>
          remaining match {
            case Nil  => ()
            case urls =>
              Thread.sleep(4000)
              tryUrls(urls.head, urls.tail)
          }
      }
    }

    tryUrls(potentialUrls.head, potentialUrls.tail)

  }

  private def guessUrl(normalizedName: String): String = baseUrl + normalizedName

  private def potentialNames(bandName: String): Set[String] = {
    val name = TextStripper.normalize(bandName)
    val underscoreName = name.replaceAll("-", "_")

    Set(name, underscoreName)
  }

  private def sendData(bandPage: BandPage): Unit = println("Formed: " + bandPage.formed.date)

}

object Processor {
  def apply(rawUrl: String): Processor = {
    val baseUrl = if (rawUrl.endsWith("/")) rawUrl else rawUrl + "/"

    new Processor(baseUrl)
  }
}
