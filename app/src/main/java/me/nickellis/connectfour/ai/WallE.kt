package me.nickellis.connectfour.ai

import me.nickellis.connectfour.*
import me.nickellis.connectfour.data.Piece
import me.nickellis.connectfour.data.ReadOnlyBoard
import kotlin.math.abs

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
    val againstRating = Piece.values()
      .filter { it != Piece.Empty && it != piece }
      .map { Pair(it, pieces.putPiece(move, it).allWinLines(toWin)) }
      .sumBy {
        val piece = it.first
        it.second.sumBy {
          val consecutive = it.consecutive(piece)
          if (consecutive >= toWin) Move.PreventWin.weight
          else Move.Neutral.weight
        }
      }

    val forRating = pieces.putPiece(move, piece)
      .allWinLines(4)
      .sumBy {
        it.consecutive()
          .map {
            when(it.first) {
              piece -> {
                if (it.second >= toWin) return Int.MAX_VALUE
                when (it.second) {
                  2 -> Move.Two
                  3 -> Move.Three
                  else -> Move.Neutral
                }
              }
              Piece.Empty -> Move.Neutral
              else -> Move.Neutral
            }
          }
          .sumBy { it.weight }
      }

    val middle = pieces.columnCount/2f
    val middleRating = (middle - abs(move - middle)).toInt() * Move.NearMiddle.weight

    return againstRating + forRating + middleRating
  }
}

enum class Move(val weight: Int) {
  //Win(100000), this is always max
  PreventWin(100000),
  Three(300),
  Two(100),
  Neutral(0),
  NearMiddle(10)
}