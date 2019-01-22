package com.music

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.HttpMethods.POST
import akka.http.scaladsl.model.{HttpRequest, RequestEntity}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.music.model.BandPage
import com.music.utils.TextStripper
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import io.circe.{Json, Printer}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

case class Processor(baseURL: String, apiURL: String) {
  def process(artists: Set[String])(implicit system: ActorSystem): Future[Done] = {
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    println("*** Processing ***")

    Source(artists)
      .mapConcat(artist => potentialNames(artist).map(guessUrl))
      .throttle(1, 4.second) // courtesy period, not to flood the website with many requests
      .runWith(Sink.foreach(processOne))
  }

  private def processOne(url: String): Unit = Scraper(url).foreach { scraper =>
    println(url)
    val bandPage = scraper.buildObject
    sendData(bandPage)
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
