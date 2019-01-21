package com.music

import com.music.utils.ProjectConfiguration
import com.music.utils.ProjectConfiguration._

import scala.io.Source
import scala.util.{Failure, Success, Try}

object MainApplication extends App {
  ProjectConfiguration
    .projectConfiguration()
    .fold(
      Launcher.showConfigError, { configuration: ProjectConfig =>
        val processor = Processor(configuration.appConfig.base_url, configuration.appConfig.api_url)
        val artists = fetchArtists(configuration.appConfig.input_file)

        processor.process(artists)
      }
    )

  final private def fetchArtists(path: String): Set[String] = Try(Source.fromFile(path)) match {
    case Success(src) => src.getLines.map(_.toLowerCase).toSet
    case Failure(_) =>
      println("Could not open file located at " + path)
      Set.empty[String]
  }
}
