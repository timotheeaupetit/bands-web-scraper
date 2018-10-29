package com.music.model

case class BandPage(name: String,
                    formed: Formed,
                    disbanded: Disbanded,
                    members: List[Member] = List.empty,
                    albums: List[Release] = List.empty)

case class Release(title: String, date: String)
