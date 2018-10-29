package com.music

import com.music.utils.ProjectConfiguration._
import com.music.model.{BandPage, Disbanded, Formed, Member}
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract.elementList
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.{Document, Element}
import net.ruippeixotog.scalascraper.scraper.ContentExtractors._

class Scraper(configuration: ProjectConfig) {

  def inferBandUrl(bandName: String): String = {
    val baseUrl = configuration.appConfig.src_url

    baseUrl + normalizeBandName(bandName)
  }

  final def normalizeBandName(bandName: String): String = {
//    bandName.toLowerCase
//      .replace("&", "and")
//      .replace("(", "")
//      .replace(")", "")
//      .map {
//        case ' ' | '¿' | '?' | '¡' | '!' => '_'
//        case 'á' | 'à' | 'ä' | 'â' | 'å' => 'a'
//        case 'é' | 'è' | 'ë' | 'ê'       => 'e'
//        case 'í' | 'ì' | 'ï' | 'î'       => 'i'
//        case 'ó' | 'ò' | 'ö' | 'ô' | 'ø' => 'o'
//        case 'ú' | 'ù' | 'ü' | 'û'       => 'u'
//        case 'ç'                         => 'c'
//        case 'ñ'                         => 'n'
//      }
    ""
  }

  def getPage(url: String): Document = {
    val browser = JsoupBrowser()
    browser.get(url)
  }

}

object Scraper {

  private def getBandName(document: Document): String =
    document >> element("h1.artist_name_hdr") >> text

  private def getFormed(info: Map[String, Element]): Formed = {
    val rawFormed = info("Formed") >> elementList(".info_content") >> text
    rawFormed.map(Formed.apply).head
  }

  private def getDisbanded(info: Map[String, Element]): Disbanded = {
    val rawDisbanded = info("Disbanded") >> elementList(".info_content") >> text
    rawDisbanded.map(Disbanded.apply).head
  }

  private def getMembers(info: Map[String, Element]): List[Member] = {
    val rawMembers = info("Members") >> element(".info_content") >> text

    val members = rawMembers.split("\\),\\s").toList

    members.map(Member.apply)
  }

  private def getDiscography(document: Document): List[Element] = {
    document >> elementList("#disco_type_s")
  }

  private def getArtistInfo(document: Document): Map[String, Element] = {
    val info = document >> element(".section_artist_info .artist_info")
    val headers = getHeaders(info)
    val details = getDetails(info)

    headers.zip(details).toMap
  }

  private def getHeaders(element: Element): List[String] =
    element >> elementList(".info_hdr") >> text

  private def getDetails(element: Element): List[Element] =
    element >> elementList(".info_content")

  def buildFromDocument(document: Document): BandPage = {

    val info = getArtistInfo(document)

    BandPage(name = getBandName(document),
             formed = getFormed(info),
             disbanded = getDisbanded(info),
             members = getMembers(info))
  }
}
