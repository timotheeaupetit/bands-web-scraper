package com.music

import akka.event.Logging
import akka.event.Logging.Warning
import cats.data.NonEmptyList
import com.music.utils.EnvironmentVariables
import com.music.utils.EnvironmentVariables.ConfigError

object Launcher {

  def showConfigError(listErr: NonEmptyList[ConfigError]): Unit = {
    val err = EnvironmentVariables.showNelConfigError.show(listErr)
    Logging.StandardOutLogger.warning(
      Warning(logSource = "Launcher",
              logClass = MainApplication.getClass,
              message = s"Cannot load configuration: $err"))
  }

}
