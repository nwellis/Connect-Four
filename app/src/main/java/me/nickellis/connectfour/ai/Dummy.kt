package me.nickellis.connectfour.ai

import me.nickellis.connectfour.Player
import me.nickellis.connectfour.data.Piece
import me.nickellis.connectfour.data.ReadOnlyBoard


class Dummy(piece: Piece) : Player(piece, false), AI {

  override fun makeMove(board: ReadOnlyBoard): Int {
    return (0 until 6).indexOfFirst { board.getColumn(it).size < board.numOfRows() }
  }

  override fun toString(): String = "Dummy"
}