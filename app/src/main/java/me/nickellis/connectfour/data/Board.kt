package me.nickellis.connectfour.data

import me.nickellis.connectfour.Player
import me.nickellis.connectfour.allWinLines
import me.nickellis.connectfour.isFull

class Board(
  val numRows: Int = 6,
  val numColumns: Int = 7,
  val toWin: Int = 4
) : ReadOnlyBoard {

  private val pieceToStart = Piece.Black

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

  private var player1: Player? = null
  private var player2: Player? = null
  private var winners: MutableList<Player> = mutableListOf()

  private val players get() = listOf(player1, player2)
  private var columns = MutableList(numColumns) { MutableList(numRows) { Piece.Empty } }

  private var whosTurn: Piece? = pieceToStart
  fun whosTurn(): Player? = players.firstOrNull { it?.piece == whosTurn }

  fun tryMakeMove(c: Int, piece: Piece): String? {
    return when {
      winners.isNotEmpty() -> "Someone has already won"
      player1 == null || player2 == null -> "Need two players"
      piece != whosTurn -> "It's not your turn!"
      columns[c].none { it == Piece.Empty } -> "This column is full"
      else -> {
        val r = columns[c].indexOfFirst { it == Piece.Empty }
        columns[c][r] = piece

        winners.addAll(computeWinners())
        if (winners.isNotEmpty()) {
          whosTurn = null
          onWin?.invoke(winners)
        }

        whosTurn = when {
          winners.isNotEmpty() -> null
          piece == Piece.Black -> Piece.Red
          else -> Piece.Black
        }

        onMoveMade?.invoke(c, r, piece)

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
    columns = columns.map { it.map { Piece.Empty }.toMutableList() }.toMutableList()
    player1 = null
    player2 = null
    whosTurn = pieceToStart
    winners.clear()
    onReset?.invoke()
  }

  private fun playerWith(piece: Piece): Player? {
    return when(piece) {
      player1?.piece -> player1
      player2?.piece -> player2
      else -> null
    }
  }

  private fun computeWinners(): List<Player> {
    val possibleWins = columns.allWinLines(toWin)

    val winners = possibleWins.mapNotNull { checkForWinner(it) }
    return if (winners.isEmpty() && columns.isFull) {
      //Draw, everyone wins!!!
      listOfNotNull(player1, player2)
    } else {
      winners
    }
  }

  private fun checkForWinner(pieces: List<Piece>): Player? {
    if (pieces.size < toWin) return null

    val consecutive = mutableMapOf(
      Pair(Piece.Black, 0),
      Pair(Piece.Red, 0)
    )

    pieces.forEach { piece ->
      consecutive
        .filter { it.key != piece }
        .forEach { consecutive[it.key] = 0 }
      consecutive[piece] = (consecutive[piece] ?: 0) + 1

      if ((consecutive[piece] ?: 0) >= toWin) return playerWith(piece)
    }

    return null
  }

  override fun numOfCols(): Int = numColumns
  override fun numOfRows(): Int = numRows
  override fun toWin(): Int = toWin
  override fun pieces(): List<List<Piece>> = columns.map { it.toList() }
}