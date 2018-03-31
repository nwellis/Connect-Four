package me.nickellis.connectfour.data

import me.nickellis.connectfour.Player
import java.util.*

class Board(
  val numRows: Int = 6,
  val numColumns: Int = 7,
  val toWin: Int = 4
) : ReadOnlyBoard {

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
  private var winners: MutableList<Player> = mutableListOf()

  private val players get() = listOf(player1, player2)
  private val columns = Array(numOfCols()) { Stack<Piece>() }

  private var whosTurn = Piece.Black
  fun whosTurn(): Player? = players.firstOrNull { it?.piece == whosTurn }

  fun look(column: Int): Piece? {
    return when(columns[column].size) {
      0 -> null
      else -> columns[column].peek()
    }
  }

  fun tryMakeMove(column: Int, piece: Piece): String? {
    return when {
      winners.isNotEmpty() -> "Someone has already won"
      player1 == null || player2 == null -> "Need two players"
      piece != whosTurn -> "It's not your turn!"
      columns[column].size == numOfRows() -> "This column is full"
      else -> {
        whosTurn = if (piece == Piece.Black) Piece.Red else Piece.Black
        columns[column].push(piece)
        onMoveMade?.invoke(numRows - columns[column].size, column, piece)

        winners.addAll(computeWinners())
        if (winners.isNotEmpty()) {
          onWin?.invoke(winners)
        }

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

  fun computeWinners(): List<Player> {
    val possibleWins = mutableListOf<List<Piece>>()

    //Vertical Win
    possibleWins.addAll(columns.map { it.toList() }) //easy :)

    //Horizontal Win
    possibleWins.addAll((0 until numRows).map { r ->
      traverse(0, r, 1, 0)
    })

    //Upward Diagonal Win
    possibleWins.addAll((numRows-1 downTo toWin-1).map { r ->
      traverse(0, r, 1, -1)
    })
    possibleWins.addAll((1..numColumns-toWin).map { c ->
      traverse(c, 0, 1, -1)
    })

    //Downward Diagonal Win
    possibleWins.addAll((0..numRows-toWin).map { r ->
      traverse(0, r, 1, 1)
    })
    possibleWins.addAll((1..numColumns-toWin).map { c ->
      traverse(c, 0, 1, 1)
    })

    val winners = possibleWins.mapNotNull { checkForConsecutive(it) }
    return if (winners.isEmpty() && columns.sumBy { it.size } == numRows * numColumns) {
      //Draw, everyone wins!!!
      listOfNotNull(player1, player2)
    } else {
      winners
    }
  }

  private fun traverse(
    startC: Int,
    startR: Int,
    deltaX: Int,
    deltaY: Int
  ): List<Piece> {
    val pieces = mutableListOf<Piece>()

    var c = startC
    var r = startR
    while (r in (0 until numRows) && c in (0 until numColumns)) {
      pieces.add(getPiece(c, r) ?: Piece.Empty)
      c += deltaX
      r += deltaY
    }

    return pieces
  }

  private fun checkForConsecutive(pieces: List<Piece>): Player? {
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

      if (consecutive[piece] == toWin) return playerWith(piece)
    }

    return null
  }

  private fun getPiece(c: Int, r: Int): Piece? {
    return columns.getOrNull(c)?.getOrNull(r)
  }
}