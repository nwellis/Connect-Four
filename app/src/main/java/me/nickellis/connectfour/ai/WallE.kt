package me.nickellis.connectfour.ai

import me.nickellis.connectfour.Player
import me.nickellis.connectfour.data.Piece
import me.nickellis.connectfour.data.ReadOnlyBoard

/**
 * An example AI to be used as a reference.
 */
class WallE(piece: Piece) : Player(piece, false), AI {

  override suspend fun makeMove(board: ReadOnlyBoard): Int {
    return board.pieces()
      .mapIndexed { c, column -> Pair(c, column) }
      .filter { it.second.contains(Piece.Empty) }
      .map {
        val lines = board.linesAtPoint(it.first, it.second.size)
        Pair(it.first, rateMove(lines, it.first))
      }
      .maxBy { it.second }?.first ?: 0
  }

  override fun toString(): String = "Wall-E"

  private fun rateMove(lines: List<List<Piece>>, move: Int): Int {
    return lines
      .flatMap { it }
      .count { it == piece }
  }
}