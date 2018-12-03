package com.music.model

case class BandPage(name: String,
                    formed: Formed,
                    disbanded: Disbanded,
                    members: List[Member] = List.empty[Member],
                    albums: List[Release] = List.empty[Release])

case class Release(title: String, date: Option[Int])
