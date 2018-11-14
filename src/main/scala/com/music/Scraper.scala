package com.music

import com.music.model.{BandPage, Disbanded, Formed, Member}
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract.elementList
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.{Document, Element}
import net.ruippeixotog.scalascraper.scraper.ContentExtractors._

import scala.util.Try

case class Scraper(document: Document) {

  private def getBandName: String = document >> element("h1.artist_name_hdr") >> text

  private def getFormed(info: Map[String, Element]): Formed =
    Try(info("Formed") >> elementList(".info_content") >> text).toOption match {
      case Some(raw) => raw.map(Formed.apply).head
      case _         => Formed(None)
    }

  private def getDisbanded(info: Map[String, Element]): Disbanded =
    Try(info("Disbanded") >> elementList(".info_content") >> text).toOption match {
      case Some(raw) => raw.map(Disbanded.apply).head
      case _         => Disbanded(None)
    }

  private def getMembers(info: Map[String, Element]): List[Member] = {
    val rawMembers = info("Members") >> element(".info_content") >> text

    val members = rawMembers.split("\\),\\s").toList

    members.map(Member.apply)
  }

  private def getDiscography: List[Element] = document >> elementList("#disco_type_s")

  private def getArtistInfo: Map[String, Element] = {
    val info = document >> element(".section_artist_info .artist_info")
    val headers = getHeaders(info)
    val details = getDetails(info)

    headers.zip(details).toMap
  }

  private def getHeaders(element: Element): List[String] =
    element >> elementList(".info_hdr") >> text

  private def getDetails(element: Element): List[Element] =
    element >> elementList(".info_content")

  def buildObject: BandPage = {

    val info = getArtistInfo

    BandPage(name = getBandName, formed = getFormed(info), disbanded = getDisbanded(info), members = getMembers(info))
  }
}

object Scraper {
  def apply(pageUrl: String): Option[Scraper] = {
    val browser = JsoupBrowser()
    val maybeDocument = Try(browser.get(pageUrl)).toOption

    maybeDocument.map(document => Scraper(document))
  }

}
