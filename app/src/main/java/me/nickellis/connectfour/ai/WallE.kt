package me.nickellis.connectfour.ai

import me.nickellis.connectfour.Player
import me.nickellis.connectfour.allWinLines
import me.nickellis.connectfour.consecutive
import me.nickellis.connectfour.data.Piece
import me.nickellis.connectfour.data.ReadOnlyBoard
import me.nickellis.connectfour.putPiece

/**
 * An example AI to be used as a reference.
 */
class WallE(piece: Piece) : Player(piece, false), AI {

  override fun toString(): String = "Wall-E"

  override suspend fun makeMove(board: ReadOnlyBoard): Int {
    val pieces = board.pieces()
    return pieces
      .mapIndexed { c, column -> Pair(c, column) }
      .filter { it.second.contains(Piece.Empty) }
      .maxBy { rate(pieces, it.first, board.toWin()) }?.first!! //I want it to crash if this is null
  }

  private fun rate(pieces: List<List<Piece>>, move: Int, toWin: Int): Int {
    return pieces.putPiece(move, piece)
      .allWinLines(4)
      .sumBy {
        it.consecutive()
          .map {
            when(it.first) {
              piece -> {
                when (it.second) {
                  2 -> Move.Two
                  3 -> Move.Three
                  toWin -> Move.Win
                  else -> Move.Neutral
                }
              }
              Piece.Empty -> Move.Neutral
              else -> Move.Neutral
            }
          }
          .sumBy { it.weight }
      }
  }
}

enum class Move(val weight: Int) {
  Win(1000),
  PreventWin(Win.weight - 1),
  Three(300),
  Two(100),
  Neutral(0)
}