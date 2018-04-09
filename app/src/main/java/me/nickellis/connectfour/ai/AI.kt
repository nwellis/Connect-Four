package me.nickellis.connectfour.ai

import me.nickellis.connectfour.data.ReadOnlyBoard


interface AI {
  /**
   * Every AI needs to implement this. A pieces which has at least one valid move is given, and the
   * AI must choose a column to place their piece in.
   * @param board A read-only pieces that has at least one valid move
   * @return The column to place a piece in, 0 being the leftmost column. The column index must
   *  be within to total column count.
   */
  suspend fun makeMove(board: ReadOnlyBoard): Int
}