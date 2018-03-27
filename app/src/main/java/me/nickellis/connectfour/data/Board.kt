package me.nickellis.connectfour.data

import me.nickellis.connectfour.Player
import java.util.*

class Board(val numRows: Int = 6, val numColumns: Int = 7) : ReadOnlyBoard {

  private var onMoveMade: ((row: Int, col: Int, piece: Piece) -> Unit)? = null
  fun onMoveMade(l: (row: Int, col: Int, piece: Piece) -> Unit) {
    onMoveMade = l
  }

  // null for draw
  private var onWin: ((winners: List<Player>) -> Unit)? = null
  fun onWin(l: (winners: List<Player>) -> Unit) {
    onWin = l
  }

  private var onReset: (() -> Unit)? = null
  fun onReset(l: () -> Unit) {
    onReset = l
  }

  override fun getColumn(column: Int): List<Piece> = columns[column].map { it }
  override fun numOfCols(): Int = numColumns
  override fun numOfRows(): Int = numRows

  private var player1: Player? = null
  private var player2: Player? = null

  private val players get() = listOf(player1, player2)
  private val columns = Array(numOfCols()) { Stack<Piece>() }

  private var whosTurn = Piece.Black
  fun whosTurn(): Player? = players.firstOrNull { it?.piece == whosTurn }

  fun look(column: Int): Piece = columns[column].peek()

  fun tryMakeMove(column: Int, piece: Piece): String? {
    return when {
      player1 == null || player2 == null -> "Need two players"
      piece != whosTurn -> "It's not your turn!"
      columns[column].size == numOfRows() -> "This column is full"
      else -> {
        whosTurn = if (piece == Piece.Black) Piece.Red else Piece.Black
        columns[column].push(piece)
        onMoveMade?.invoke(numRows - columns[column].size, column, piece)
        return null
      }
    }
  }

  fun addPlayer(player: Player) {
    when {
      player1 == null || player1?.piece == player.piece -> player1 = player
      player2 == null || player2?.piece == player.piece -> player2 = player
    }
  }

  fun reset() {
    columns.forEach { it.clear() }
    player1 = null
    player2 = null
    onReset?.invoke()
  }

  fun winner(): Player? {
    return player1
  }
}