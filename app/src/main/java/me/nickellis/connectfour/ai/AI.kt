package me.nickellis.connectfour.ai

import me.nickellis.connectfour.data.ReadOnlyBoard


interface AI {
  fun makeMove(board: ReadOnlyBoard): Int
}