package me.nickellis.connectfour.data

import me.nickellis.connectfour.Human
import me.nickellis.connectfour.Player
import me.nickellis.connectfour.ai.Dummy


fun getNewPlayers(humanName: String = "Player"): List<Player> {
  return listOf(
    Human(humanName),
    Dummy()
  )
}