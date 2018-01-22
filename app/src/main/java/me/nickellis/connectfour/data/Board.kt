package me.nickellis.connectfour.data

import java.util.*

class Board : ReadOnlyBoard {

  override fun getColumn(column: Int): List<Piece> = columns[column].map { it }
  override fun numOfCols(): Int = 7
  override fun numOfRows(): Int = 6

  private val columns = Array(numOfCols()) { Stack<Piece>() }

  private var whosTurn = Piece.Black
  fun whosTurn() = whosTurn

  fun peek(column: Int): Piece = columns[column].peek()

  fun put(column: Int, piece: Piece) {
    if (piece != whosTurn || columns[column].size == numOfRows()) return
    columns[column].push(piece)
    whosTurn = if (piece == Piece.Black) Piece.Red else Piece.Black
  }




}