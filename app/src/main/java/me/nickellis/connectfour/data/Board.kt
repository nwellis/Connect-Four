package me.nickellis.connectfour.data

import me.nickellis.connectfour.Player
import java.util.*
import kotlin.math.max
import kotlin.math.min

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
  private val columns = Array(numOfCols()) { Stack<Piece>() }

  private var whosTurn: Piece? = pieceToStart
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
        columns[column].push(piece)

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
    val possibleWins = allLines()

    val winners = possibleWins.mapNotNull { checkForConsecutive(it) }
    return if (winners.isEmpty() && columns.sumBy { it.size } == numRows * numColumns) {
      //Draw, everyone wins!!!
      listOfNotNull(player1, player2)
    } else {
      winners
    }
  }

  private fun allLines(): List<List<Piece>> {
    val lines = mutableListOf<List<Piece>>()

    //Vertical Win
    lines.addAll(columns.map { it.toList() }) //easy :)

    //Horizontal Win
    lines.addAll((0 until numRows).map { r ->
      traverse(0, r, 1, 0)
    })

    //Upward Diagonal Win
    lines.addAll((numRows-1 downTo toWin-1).map { r ->
      traverse(0, r, 1, -1)
    })
    lines.addAll((1..numColumns-toWin).map { c ->
      traverse(c, 0, 1, -1)
    })

    //Downward Diagonal Win
    lines.addAll((0..numRows-toWin).map { r ->
      traverse(0, r, 1, 1)
    })
    lines.addAll((1..numColumns-toWin).map { c ->
      traverse(c, 0, 1, 1)
    })

    return lines
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

      if ((consecutive[piece] ?: 0) >= toWin) return playerWith(piece)
    }

    return null
  }

  override fun numOfCols(): Int = numColumns
  override fun numOfRows(): Int = numRows

  override fun pieces(): List<List<Piece>> = columns
    .map { it.toMutableList() }
    .map {
      while (it.size < numRows) it.add(Piece.Empty)
      it.toList()
    }

  override fun getPiece(c: Int, r: Int): Piece {
    return columns.getOrNull(c)?.getOrNull(r) ?: Piece.Empty
  }

  override fun traverse(
    startC: Int,
    startR: Int,
    deltaX: Int,
    deltaY: Int
  ): List<Piece> {
    val pieces = mutableListOf<Piece>()

    var c = startC
    var r = startR
    while (r in (0 until numRows) && c in (0 until numColumns)) {
      pieces.add(getPiece(c, r))
      c += deltaX
      r += deltaY
    }

    return pieces
  }

  override fun linesAtPoint(c: Int, r: Int): List<List<Piece>> {
    val topC = max(r - c, numColumns - 1)
    val topR = min(r + c, numRows - 1)
    val botC = max(c - r, 0)
    val botR = max(r - c, 0)

    return listOf(
      traverse(0, r, 1, 0), //horizontal
      traverse(c, 0, 0, 1), //vertical
      traverse(topC, topR, -1, -1), //downward diagonal
      traverse(botC, botR, 1, 1) //upward diagonal
    )
  }
}