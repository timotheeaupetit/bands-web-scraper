package com.music.scraper

import com.music.model.{BandPage, Disbanded, Formed, Member}
import io.circe._
import io.circe.generic.semiauto.deriveDecoder
import io.circe.parser.decode

import scala.io.Source

case class DiscogsScraper(bandJson: BandJson) extends Scraper {

  def getBandName: String = bandJson.name

  def getFormed: Formed = Formed(date = None)

  def getDisbanded: Disbanded = Disbanded(date = None)

  def getMembers: List[Member] = bandJson.members.map(obj => Member(fullName = obj.name, isActive = obj.active))

  override def buildObject: BandPage = {
    BandPage(name = getBandName, formed = getFormed, disbanded = getDisbanded, members = getMembers)
  }
}

case class MemberJson(name: String, active: Boolean)
case class BandJson(name: String, profile: String, members: List[MemberJson])

object DiscogsScraper {
  def apply(artistId: String): DiscogsScraper = {
    val url = "https://api.discogs.com"+ "/artist/" + artistId
    val res = Source.fromURL(url).mkString
    implicit val nestedDecoder: Decoder[MemberJson] = deriveDecoder[MemberJson]
    implicit val jsonDecoder: Decoder[BandJson] = deriveDecoder[BandJson]
    val decoded = decode[BandJson](res)
    decoded match {
      case Left(parsingError) => throw new IllegalArgumentException(s"Invalid JSON object: ${parsingError}")
      case Right(bandJson) => DiscogsScraper(bandJson)
    }
  }

}
