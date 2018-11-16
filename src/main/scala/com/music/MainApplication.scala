package com.music

import com.music.utils.ProjectConfiguration
import com.music.utils.ProjectConfiguration._

object MainApplication extends App {
  ProjectConfiguration
    .projectConfiguration()
    .fold(
      Launcher.showConfigError, { configuration: ProjectConfig =>
        println(configuration.appConfig.application)
        val processor = Processor(configuration.appConfig.base_url)
        processor.process()
      }
    )

}
