package com.music.scraper

import com.music.model.{BandPage, Disbanded, Formed, Member}
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract.elementList
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.{Document, Element}
import net.ruippeixotog.scalascraper.scraper.ContentExtractors._

import scala.util.Try

case class RYMScraper(document: Document) extends Scraper {

  override def getBandName: String = document >> element("h1.artist_name_hdr") >> text

  def getFormed(info: Map[String, Element]): Formed =
    Try(info("Formed") >> elementList(".info_content") >> text).toOption match {
      case Some(raw) => raw.map(Formed.apply).head
      case _         => Formed(None)
    }

  def getDisbanded(info: Map[String, Element]): Disbanded =
    Try(info("Disbanded") >> elementList(".info_content") >> text).toOption match {
      case Some(raw) => raw.map(Disbanded.apply).head
      case _         => Disbanded(None)
    }

  def getMembers(info: Map[String, Element]): List[Member] = {
    val rawMembers = info("Members") >> element(".info_content") >> text

    val members = rawMembers.split("\\),\\s").toList

    members.map(Member.apply)
  }

  def getDiscography: List[Element] = document >> elementList("#disco_type_s")

  def getArtistInfo: Map[String, Element] = {
    val info = document >> element(".section_artist_info .artist_info")
    val headers = getHeaders(info)
    val details = getDetails(info)

    headers.zip(details).toMap
  }

  def getHeaders(element: Element): List[String] =
    element >> elementList(".info_hdr") >> text

  def getDetails(element: Element): List[Element] =
    element >> elementList(".info_content")

  override def buildObject: BandPage = {

    val info = getArtistInfo

    BandPage(name = getBandName, formed = getFormed(info), disbanded = getDisbanded(info), members = getMembers(info))
  }
}

object RYMScraper {
  def apply(pageUrl: String): Option[RYMScraper] = {
    val browser = JsoupBrowser()
    val maybeDocument = Try(browser.get(pageUrl)).toOption

    maybeDocument.map(document => RYMScraper(document))
  }

}
