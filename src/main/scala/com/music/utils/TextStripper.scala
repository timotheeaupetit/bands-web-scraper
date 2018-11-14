package com.music.utils

object TextStripper {
  private val ignored: Map[Char, String] = Map('(' -> "", ')' -> "", '+' -> "", ''' -> "", '’' -> "")

  private val composites: Map[Char, String] = Map('æ' -> "ae", 'Æ' -> "AE", 'œ' -> "oe", 'Œ' -> "OE")

  private val special: String = "ÀÁÂÃÄÅÇÈÉÊËÌÍÎÏÒÓÔÕÖØÙÚÛÜÑ" + "àáâãäåçèéêëìíîïòóôõöøùúûüñ" + " " + "¿?¡!/-,."
  private val stripped: String = "AAAAAACEEEEIIIIOOOOOOUUUUN" + "aaaaaaceeeeiiiioooooouuuun" + "-" + "________"

  private val substitutes: Map[Char, String] = special.zip(stripped.map(_.toString)).toMap

  def normalize(text: String): String = {
    text.toLowerCase
      .replaceAll("&", "and")
      .flatMap(c => ignored.getOrElse(c, c.toString))
      .flatMap(c => composites.getOrElse(c, c.toString))
      .flatMap(c => substitutes.getOrElse(c, c.toString))
  }
}
