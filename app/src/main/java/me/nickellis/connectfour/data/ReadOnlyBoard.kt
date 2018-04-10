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
}