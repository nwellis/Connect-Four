package me.nickellis.connectfour.ai

import me.nickellis.connectfour.data.ReadOnlyBoard


class Dummy : AI {
  override fun makeMove(board: ReadOnlyBoard): Int {
    return (0 until 6).indexOfFirst { board.getColumn(it).size < board.numOfRows() }
  }
}