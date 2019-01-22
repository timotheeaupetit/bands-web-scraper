package com.music

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.music.utils.ProjectConfiguration
import com.music.utils.ProjectConfiguration._

import scala.concurrent.ExecutionContextExecutor
import scala.io.Source
import scala.util.{Failure, Success, Try}

object MainApplication extends App {
  ProjectConfiguration
    .projectConfiguration()
    .fold(Launcher.showConfigError, process)

  final private def process: ProjectConfig => Unit = { configuration: ProjectConfig =>
    implicit val system: ActorSystem = ActorSystem("Processor")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val ec: ExecutionContextExecutor = system.dispatcher

    val processor = Processor(configuration.appConfig.base_url, configuration.appConfig.api_url)
    val artists = fetchArtists(configuration.appConfig.input_file)

    processor.process(artists).onComplete {
      case Success(_) =>
        println("*** Done ***")
        system.terminate
      case Failure(_) => println("Failure")
    }
  }

  final private def fetchArtists(path: String): Set[String] = Try(Source.fromFile(path)) match {
    case Success(src) => src.getLines.map(_.toLowerCase).toSet
    case Failure(_) =>
      println("Could not open file located at " + path)
      Set.empty[String]
  }
}
