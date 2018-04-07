package me.nickellis.connectfour.ai

import me.nickellis.connectfour.Player
import me.nickellis.connectfour.data.Piece
import me.nickellis.connectfour.data.ReadOnlyBoard
import java.util.Random

/**
 * An example AI to be used as a reference.
 */
class Random(piece: Piece) : Player(piece, false), AI {

  override suspend fun makeMove(board: ReadOnlyBoard): Int {
    val moves = (0 until board.numOfCols())
      .map { Pair(it, board.getColumn(it)) }
      .filter { it.second.size < board.numOfRows() }

    return moves[Random().nextInt(moves.size)].first
  }

  override fun toString(): String = "Random"
}