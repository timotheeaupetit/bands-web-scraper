package com.music

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import org.specs2.Specification
import org.specs2.matcher.MatchResult
import org.specs2.specification.core.SpecStructure

class ScraperSpec extends Specification {
  def is: SpecStructure =
    s2"""
        | The band name should be 'A Perfect Circle'            $name
        | The year the band was formed should be '1999'         $formed
        | The year the band was disbanded should be 'None'      $disbanded
        | There should be 11 members extracted from content     $members
        | There should be 'Maynard James Keenan' among them     $maynard
        | Jeordie White's aka should be 'Twiggy Ramirez'        $aka
        | Jeordie White left the band in '2004'                 $period
      """.stripMargin

  private val browser = JsoupBrowser()

  private val htmlPage =
    """<!DOCTYPE html>
      |<html class="theme_">
      |<head>
      |    <title>A Perfect Circle Page</title>
      |</head>
      |<body class="nolinks" style="">
      |<div id="wrapper">
      |    <div id="content">
      |        <div class="artist_page scope_music">
      |            <div class="row">
      |                <div class="large-8 push-4 columns artist_left_col">
      |                    <div class="section_artist_name">
      |                        <div class="artist_name">
      |                            <h1 class="artist_name_hdr">A Perfect Circle</h1>
      |                        </div>
      |                    </div>
      |                    <div class="section_artist_info">
      |                        <div class="artist_info">
      |                            <div class="info_hdr">Formed</div>
      |                            <div class="info_content"> 1999, <a class="location" href="#">Los Angeles, CA, United States</a></div>
      |                            <div class="info_hdr">Members</div>
      |                            <div class="info_content">
      |                                <a title="[Artist579837]" href="#" class="artist">Maynard James Keenan</a> (lead vocals, piano, kalimba, guitar),
      |                                <a title="[Artist955887]" href="#" class="artist">Billy Howerdel</a> (lead guitar, bass, vocals, keyboards, piano, harmonium, programming),
      |                                <a title="[Artist303942]" href="#" class="artist">Tim Alexander</a> (drums, percussion, 1999),
      |                                <a title="[Artist52219]" href="#" class="artist">Josh Freese</a> (drums, percussion, backing vocals, 1999-2011),
      |                                <a title="[Artist58981]" href="#" class="artist">Paz Lenchantin</a> (bass, strings, violin, keyboards, piano, backing vocals, 1999-2001, 2004),
      |                                <a title="[Artist976851]" href="#" class="artist">Troy Van Leeuwen</a> (rhythm guitar, 1999-2003),
      |                                Jeordie White [aka <a title="[Artist429142]" href="#" class="artist">Twiggy Ramirez</a>] (bass, backing vocals, 2003-04),
      |                                <a title="[Artist467318]" href="#" class="artist">Danny Lohner</a> (rhythm guitar, bass, keyboards, backing vocals, programming, 2003, 2004),
      |                                <a title="[Artist744]" href="#" class="artist">James Iha</a> (rhythm guitar, keyboards, backing vocals, programming, 2003-04, 2010-present),
      |                                <a title="[Artist1290108]" href="#" class="artist">Matt McJunkins</a> (bass, backing vocals, 2010-present),
      |                                <a title="[Artist1290111]" href="#" class="artist">Jeff Friedl</a> (drums, 2011-present)
      |                            </div>
      |                            <div class="info_hdr">Related Artists</div>
      |                            <div class="info_content">
      |                                <a title="[Artist1044021]" href="#" class="artist">The Beta Machine</a>,
      |                                <a title="[Artist127008]" href="#" class="artist">Black Light Burns</a>,
      |                                <a title="[Artist221369]" href="#" class="artist">The Damning Well</a>,
      |                                <a title="[Artist19883]" href="#" class="artist">The Desert Sessions</a>,
      |                                <a title="[Artist294739]" href="#" class="artist">Into the Presence</a>,
      |                                <a title="[Artist225]" href="#" class="artist">Nine Inch Nails</a>
      |                            </div><div class="info_hdr">Genres</div>
      |                            <div class="info_content">
      |                                <a title="[Genre116]" class="genre" href="#">Alternative Rock</a>,
      |                                <a title="[Genre427]" class="genre" href="#">Industrial Rock</a>,
      |                                <a title="[Genre82]" class="genre" href="#">Electronic</a>,
      |                                <a title="[Genre163]" class="genre" href="#">Acoustic Rock</a>
      |                            </div>
      |                        </div>
      |                    </div>
      |                    <div class="section_artist_discography">
      |                        <div id="discography">
      |                            <div class="disco_header_top">
      |                                <h3 class="disco_header_label">Album</h3>
      |                            </div>
      |                            <div id="disco_type_s">
      |                                <div class="disco_release" id="release_1601">
      |                                    <div class="disco_info">
      |                                        <div class="disco_mainline">
      |                                            <b title="Recommended">
      |                                            <a title="[Album1601]" href="#" class="album">Mer de noms</a>
      |                                            </b>
      |                                        </div>
      |                                        <div class="disco_subline">
      |                                            <span title="23 May 2000 " class="disco_year_ymd">2000</span>
      |                                        </div>
      |                                    </div>
      |                                </div>
      |                                <div class="disco_release" id="release_73279">
      |                                    <div class="disco_info">
      |                                        <div class="disco_mainline">
      |                                            <a title="[Album73279]" href="#" class="album">Thirteenth Step</a>
      |                                        </div>
      |                                        <div class="disco_subline">
      |                                            <span title="16 September 2003 " class="disco_year_ymd">2003</span>
      |                                        </div>
      |                                    </div>
      |                                </div>
      |                                <div class="disco_release" id="release_182320">
      |                                    <div class="disco_info">
      |                                        <div class="disco_mainline">
      |                                            <a title="[Album182320]" href="#" class="album">eMOTIVe</a>
      |                                        </div>
      |                                        <div class="disco_subline">
      |                                            <span title="2 November 2004 " class="disco_year_ymd">2004</span>
      |                                        </div>
      |                                    </div>
      |                                </div>
      |                                <div class="disco_release" id="release_8969411">
      |                                    <div class="disco_info">
      |                                        <div class="disco_mainline">
      |                                            <a title="[Album8969411]" href="#" class="album">Eat the Elephant</a>
      |                                        </div>
      |                                        <div class="disco_subline">
      |                                            <span title="20 April 2018 " class="disco_year_ymd">2018</span>
      |                                        </div>
      |                                    </div>
      |                                </div>
      |                            </div>
      |                        </div>
      |                    </div>
      |                </div>
      |            </div>
      |        </div>
      |    </div>
      |</div>
      |</body></html>""".stripMargin

  private val content = browser.parseString(htmlPage)

  private val bandPage = Scraper(content).buildObject

  private val jeordieWhite = bandPage.members.filter(_.fullName == "Jeordie White").head

  private def name: MatchResult[String] = bandPage.name must beEqualTo("A Perfect Circle")

  private def formed: MatchResult[Option[Int]] = bandPage.formed.date must beSome(1999)

  private def disbanded: MatchResult[Option[Int]] = bandPage.disbanded.date must beNone

  private def members: MatchResult[Int] = bandPage.members.length must beEqualTo(11)

  private def maynard: MatchResult[Int] = bandPage.members.count(_.fullName == "Maynard James Keenan") must beEqualTo(1)

  private def aka: MatchResult[Option[String]] = jeordieWhite.aka must beSome("Twiggy Ramirez")

  private def period: MatchResult[Option[Int]] = jeordieWhite.periods.head.end must beSome(2004)

}
