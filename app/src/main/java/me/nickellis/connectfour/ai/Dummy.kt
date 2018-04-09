package me.nickellis.connectfour.ai

import me.nickellis.connectfour.Player
import me.nickellis.connectfour.data.Piece
import me.nickellis.connectfour.data.ReadOnlyBoard

/**
 * An example AI to be used as a reference.
 */
class Dummy(piece: Piece) : Player(piece, false), AI {

  override suspend fun makeMove(board: ReadOnlyBoard): Int {
    return board.pieces()
      .indexOfFirst { it.contains(Piece.Empty) }
  }

  override fun toString(): String = "Dummy"
}