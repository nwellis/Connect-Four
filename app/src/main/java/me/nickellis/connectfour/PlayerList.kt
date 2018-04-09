package me.nickellis.connectfour

import me.nickellis.connectfour.ai.Dummy
import me.nickellis.connectfour.ai.Random
import me.nickellis.connectfour.ai.WallE
import me.nickellis.connectfour.data.Piece


fun getNewPlayers(humanName: String = "Player", piece: Piece): List<Player> {
  return listOf(
    Human(humanName, piece),
    Dummy(piece),
    Random(piece),
    WallE(piece)
  )
}