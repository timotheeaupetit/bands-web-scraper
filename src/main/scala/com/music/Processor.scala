package com.music

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.HttpMethods.POST
import akka.http.scaladsl.model.{HttpRequest, RequestEntity}
import akka.stream.ActorMaterializer
import com.music.model.BandPage
import com.music.utils.TextStripper
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import io.circe.{Json, Printer}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}
import scala.util.{Failure, Success}

case class Processor(baseURL: String, apiURL: String) {
  def process(artists: Set[String])(implicit system: ActorSystem): Unit = {
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    println("*** Processing ***")

    artists.foreach(artist => {
      potentialNames(artist).map(guessUrl).foreach(processOne)
    })

    println("*** Done ***")
  }

  private def processOne(url: String): Unit = Scraper(url).foreach { scraper =>
    println(url)
    val bandPage = scraper.buildObject
    sendData(bandPage)
    println(bandPage.name)

    Thread.sleep(5000) // courtesy period, not to flood the website with many requests
  }

  private def guessUrl(normalizedName: String): String = baseURL + "/" + normalizedName

  private def potentialNames(bandName: String): Set[String] = {
    val name = TextStripper.normalize(bandName)
    val underscoreName = name.replaceAll("-", "_")

    Set(name, underscoreName)
  }

  private def sendData(bandPage: BandPage): Unit = {
    implicit lazy val system: ActorSystem = ActorSystem("BandsAPIClient")
    implicit lazy val materializer: ActorMaterializer = ActorMaterializer()
    implicit lazy val executionContext: ExecutionContext = scala.concurrent.ExecutionContext.global
    implicit lazy val printer: Json => String = Printer.noSpaces.copy(dropNullValues = true).pretty

    lazy val timeout: Duration = 30.seconds

    val maybeEntity = Marshal[BandPage](bandPage).to[RequestEntity]
    val entity = Await.result(maybeEntity, timeout)

    val fResponse = Http().singleRequest(HttpRequest(method = POST, uri = apiURL + "/band-page", entity = entity))

    fResponse onComplete {
      case Success(content) => println("Formed: " + bandPage.formed.date)
      case Failure(t)       => println("An error has occurred: " + t.getMessage)
    }
  }

}

object Processor {
  def apply(rawSourceURL: String, rawApiURL: String): Processor = {
    val baseURL = cleanURL(rawSourceURL)
    val apiURL = cleanURL(rawApiURL)

    new Processor(baseURL, apiURL)
  }

  private def cleanURL(rawURL: String): String = if (rawURL.endsWith("/")) rawURL.init else rawURL

}
