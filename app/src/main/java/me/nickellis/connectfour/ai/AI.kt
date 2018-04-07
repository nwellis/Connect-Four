package me.nickellis.connectfour.ai

import me.nickellis.connectfour.data.ReadOnlyBoard


interface AI {
  suspend fun makeMove(board: ReadOnlyBoard): Int
}