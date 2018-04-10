package me.nickellis.connectfour

import me.nickellis.connectfour.data.Piece
import kotlin.math.max
import kotlin.math.min

val List<List<Piece>>.columnCount get() = this.size
val List<List<Piece>>.rowCount get() = this.getOrNull(0)?.size ?: 0 //assumes a rectangle
val List<List<Piece>>.isFull get() = this.sumBy { it.count { it == Piece.Empty } } == 0

/**
 * Gets the piece at the given point
 * @param c column index
 * @param r row index
 * @return A [Piece]
 */
fun List<List<Piece>>.get(c: Int, r: Int): Piece {
  return this.getOrNull(c)?.getOrNull(r) ?: Piece.Empty
}

/**
 * Returns a copy of the board with the given piece placed in the given column
 * @param c column index to place piece in
 * @param piece piece to place in column
 * @return copy of the board with a piece added
 */
fun List<List<Piece>>.putPiece(c: Int, piece: Piece): List<List<Piece>> {
  return this.mapIndexed { index, column ->
    if (index != c) return@mapIndexed column.toList()

    var pieceSet = false
    column.map {
      if (!pieceSet && it == Piece.Empty) {
        pieceSet = true
        piece
      }
      else it
    }
  }
}

/**
 * Will traverse over the pieces given a point. Movement is determined by the delta arguments
 * @param startC column index starting point
 * @param startR row index starting point
 * @param deltaX horizontal movement to take for each step
 * @param deltaY vertical movement to take for each step
 * @return pieces that were traversed over
 */
fun List<List<Piece>>.traverse(
  startC: Int,
  startR: Int,
  deltaX: Int,
  deltaY: Int
): List<Piece> {
  val pieces = mutableListOf<Piece>()

  var c = startC
  var r = startR
  while (r in (0 until this.rowCount) && c in (0 until this.columnCount)) {
    pieces.add(this.get(c, r))
    c += deltaX
    r += deltaY
  }

  return pieces
}

/**
 * Given a point on the board, will get the horizontal, vertical, and diagonals intersecting that
 * point.
 * @param c column index
 * @param r row index
 * @return horizontal, vertical, and diagonals intersecting the point
 */
fun List<List<Piece>>.linesAtPoint(c: Int, r: Int): List<List<Piece>> {
  val topC = max(r - c, this.columnCount - 1)
  val topR = min(r + c, this.rowCount - 1)
  val botC = max(c - r, 0)
  val botR = max(r - c, 0)

  return listOf(
    traverse(0, r, 1, 0), //horizontal
    traverse(c, 0, 0, 1), //vertical
    traverse(topC, topR, -1, -1), //downward diagonal
    traverse(botC, botR, 1, 1) //upward diagonal
  )
}

/**
 * Returns all of the possible win lines in a board. This would include all the vertical,
 * horizontal, and diagonals that could have a winner. This does not check what pieces are
 * in the line, which means a line could not have a win scenario as the pieces have prevented it.
 *
 * This also assumes the board is rectangular.
 *
 * @param toWin how many pieces it takes to win
 * @return all the lines that could have a win, not taking into account the pieces in it
 */
fun List<List<Piece>>.allWinLines(toWin: Int): List<List<Piece>> {
  val lines = mutableListOf<List<Piece>>()

  //Vertical Win
  lines.addAll(this.map { it.toList() })

  //Horizontal Win
  lines.addAll((0 until this.rowCount).map { r ->
    traverse(0, r, 1, 0)
  })

  //Upward Diagonal Win
  lines.addAll((this.rowCount-1 downTo toWin-1).map { r ->
    traverse(0, r, 1, -1)
  })
  lines.addAll((1..this.columnCount-toWin).map { c ->
    traverse(c, 0, 1, -1)
  })

  //Downward Diagonal Win
  lines.addAll((0..this.rowCount-toWin).map { r ->
    traverse(0, r, 1, 1)
  })
  lines.addAll((1..this.columnCount-toWin).map { c ->
    traverse(c, 0, 1, 1)
  })

  return lines
}

/**
 * Counts the biggest number of pieces in a row. The first of the pair is the piece, while the
 * second is the count of the biggest number of that piece in a row.
 * @return mapping of pieces with the biggest number of that piece in a row.
 */
fun List<Piece>.consecutive(): List<Pair<Piece, Int>> {
  return Piece.values().map { Pair(it, this.consecutive(it)) }
}

/**
 * Counts the biggest number of items in a row.
 * @param toMatch item to match on
 * @return biggest number of items in a row
 */
fun <T> List<T>.consecutive(toMatch: T): Int {
  var max = 0
  var cur = 0

  this.forEach {
    when (it) {
      toMatch -> {
        cur++
        max = max(cur, max)
      }
      else -> cur = 0
    }
  }

  return max
}

