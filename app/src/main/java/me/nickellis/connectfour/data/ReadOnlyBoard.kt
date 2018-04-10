package me.nickellis.connectfour.data

interface ReadOnlyBoard {
  /**
   * @return Number of columns the board has
   */
  fun numOfCols(): Int

  /**
   * @return Number of rows the board has
   */
  fun numOfRows(): Int

  /**
   * Number in a row needed to win
   */
  fun toWin(): Int

  /**
   * All the current [Piece]s on the board. The first list is the list of columns of size
   * [numOfCols], the left column being the 0 index. The inner lists are a list of size [numOfRows],
   * the bottom row being the index 0.
   * @return A matrix of pieces that represent the board.
   */
  fun pieces(): List<List<Piece>>

  /**
   * Will get the horizontal, vertical, and diagonals that intersect with a given point.
   * @param c column index
   * @param r row index
   * @return A list of size 4 with lines of varying sizes.
   */
  fun linesAtPoint(c: Int, r: Int): List<List<Piece>>

  /**
   * Gets the piece at the given point
   * @param c column index
   * @param r row index
   * @return A [Piece]
   */
  fun getPiece(c: Int, r: Int): Piece

  /**
   * Will traverse over the pieces given a point. Movement is determined by the delta arguments
   * @param startC column index starting point
   * @param startR row index starting point
   * @param deltaX horizontal movement to take for each step
   * @param deltaY vertical movement to take for each step
   */
  fun traverse(
    startC: Int,
    startR: Int,
    deltaX: Int,
    deltaY: Int
  ): List<Piece>
}