package me.nickellis.connectfour.data

interface ReadOnlyBoard {
  fun numOfCols(): Int
  fun numOfRows(): Int
  fun getColumn(column: Int): List<Piece>
  fun getPiece(c: Int, r: Int): Piece
}