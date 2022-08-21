package com.music.scraper

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import io.circe.parser.decode
import org.specs2.matcher.MatchResult
import org.specs2.mutable.Specification
import org.specs2.specification.core.SpecStructure

class DiscogsScraperSpec extends Specification {
  override def is: SpecStructure =
    s2"""
        | The band name should be 'Meshuggah'                     $name
        | There should be 10 members extracted from content       $members
        | Tomas Haake status should be 'active'                   $isActive
        | Tomas Haake shouldn't have any period defined           $periods
        | Tomas Haake shouldn't have any instrument defined       $instruments
        | Gustaf Hielm status should be 'not active'              $isNotActive
        | Richard Gotainer should not be a past or active member  $richardGotainer
      """.stripMargin

  private val strObj =
    """{
      |    "name": "Meshuggah",
      |    "id": 252273,
      |    "resource_url": "https://api.discogs.com/artists/252273",
      |    "uri": "https://www.discogs.com/artist/252273-Meshuggah",
      |    "releases_url": "https://api.discogs.com/artists/252273/releases",
      |    "images": [
      |        {
      |            "type": "primary",
      |            "uri": "",
      |            "resource_url": "",
      |            "uri150": "",
      |            "width": 600,
      |            "height": 600
      |        },
      |        {
      |            "type": "secondary",
      |            "uri": "",
      |            "resource_url": "",
      |            "uri150": "",
      |            "width": 600,
      |            "height": 385
      |        }
      |    ],
      |    "profile": "Swedish progressive metal band from Umeå, which was formed in 1987.\r\n\r\nThe group's name comes from Hebrew and Yiddish and means 'crazy'.",
      |    "urls": [
      |        "https://www.meshuggah.net/",
      |        "https://www.facebook.com/meshuggah",
      |        "https://myspace.com/meshuggah",
      |        "https://twitter.com/meshuggah",
      |        "https://en.wikipedia.org/wiki/Meshuggah",
      |        "https://sv.wikipedia.org/wiki/Meshuggah"
      |    ],
      |    "namevariations": [
      |        "=",
      |        "メシュガー"
      |    ],
      |    "aliases": [
      |        {
      |            "id": 6339812,
      |            "name": "Calipash",
      |            "resource_url": "https://api.discogs.com/artists/6339812"
      |        }
      |    ],
      |    "members": [
      |        {
      |            "id": 252881,
      |            "name": "Fredrik Thordendal",
      |            "resource_url": "https://api.discogs.com/artists/252881",
      |            "active": true
      |        },
      |        {
      |            "id": 343918,
      |            "name": "Tomas Haake",
      |            "resource_url": "https://api.discogs.com/artists/343918",
      |            "active": true
      |        },
      |        {
      |            "id": 472920,
      |            "name": "Jens Kidman",
      |            "resource_url": "https://api.discogs.com/artists/472920",
      |            "active": true
      |        },
      |        {
      |            "id": 475292,
      |            "name": "Peter Nordin",
      |            "resource_url": "https://api.discogs.com/artists/475292",
      |            "active": false
      |        },
      |        {
      |            "id": 475294,
      |            "name": "Mårten Hagström",
      |            "resource_url": "https://api.discogs.com/artists/475294",
      |            "active": true
      |        },
      |        {
      |            "id": 543897,
      |            "name": "Gustaf Hielm",
      |            "resource_url": "https://api.discogs.com/artists/543897",
      |            "active": false
      |        },
      |        {
      |            "id": 665158,
      |            "name": "Dick Lövgren",
      |            "resource_url": "https://api.discogs.com/artists/665158",
      |            "active": true
      |        },
      |        {
      |            "id": 1008206,
      |            "name": "Per Nilsson (4)",
      |            "resource_url": "https://api.discogs.com/artists/1008206",
      |            "active": false
      |        },
      |        {
      |            "id": 1382324,
      |            "name": "Johan Sjögren",
      |            "resource_url": "https://api.discogs.com/artists/1382324",
      |            "active": false
      |        },
      |        {
      |            "id": 1852095,
      |            "name": "Niklas Lundgren (2)",
      |            "resource_url": "https://api.discogs.com/artists/1852095",
      |            "active": false
      |        }
      |    ],
      |    "data_quality": "Needs Vote"
      |}
      |""".stripMargin

  implicit val nestedDecoder: Decoder[MemberJson] = deriveDecoder[MemberJson]
  implicit val jsonDecoder: Decoder[BandJson] = deriveDecoder[BandJson]
  private val decoded = decode[BandJson](strObj)

  private val bandPage = decoded match {
    case Left(parsingError) => throw new IllegalArgumentException(s"Invalid JSON object: ${parsingError}")
    case Right(bandJson) => DiscogsScraper(bandJson).buildObject
  }

  private val tomasHaake = bandPage.members.filter(_.fullName == "Tomas Haake").head

  private val gustafHielm = bandPage.members.filter(_.fullName == "Gustaf Hielm").head

  private def name: MatchResult[String] = bandPage.name must beEqualTo("Meshuggah")

  private def members: MatchResult[Int] = bandPage.members.length must beEqualTo(10)

  private def isActive: MatchResult[Boolean] = tomasHaake.isActive must beTrue

  private def isNotActive: MatchResult[Boolean] = gustafHielm.isActive must beFalse

  private def periods: MatchResult[Int] = tomasHaake.periods.length must beEqualTo(0)

  private def instruments: MatchResult[Int] = tomasHaake.instruments.length must beEqualTo(0)

  private def richardGotainer: MatchResult[Int] = bandPage.members.count(_.fullName == "Richard Gotainer") must beEqualTo(0)

}
