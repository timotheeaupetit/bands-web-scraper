package com.music.scraper

import com.music.model.BandPage

trait Scraper {

  def getBandName: String

  def buildObject: BandPage

}
