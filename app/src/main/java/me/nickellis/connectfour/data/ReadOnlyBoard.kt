package me.nickellis.connectfour.data

interface ReadOnlyBoard {

  fun getColumn(column: Int): List<Piece>
  fun numOfCols(): Int
  fun numOfRows(): Int

}