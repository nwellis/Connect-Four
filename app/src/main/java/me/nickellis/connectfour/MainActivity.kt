package me.nickellis.connectfour

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import icepick.State
import kotlinx.android.synthetic.main.activity_main.*
import me.nickellis.connectfour.data.Board
import me.nickellis.connectfour.data.Piece

class MainActivity : AppCompatActivity() {

  @State @JvmField var inGame = false
  private val board = Board()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    setupBoard()
    findPlayers()

    // Initialize the board
    if (savedInstanceState == null) {
      board.reset()
      vAction.setText(R.string.begin)
      vInfo.setText(R.string.start_the_game)
    }

    vAction.setOnClickListener {
      if (inGame) {
        board.reset()
        vAction.setText(R.string.begin)
        vInfo.setText(R.string.start_the_game)
      } else {
        board.addPlayer(vPlayer1.adapter.getItem(0) as Player)
        board.addPlayer(vPlayer2.adapter.getItem(0) as Player)
        vAction.setText(R.string.cancel)
        vInfo.setText(when (board.whosTurn()?.piece) {
          Piece.Black -> R.string.blacks_turn
          else -> R.string.reds_turn
        })
      }
      inGame = !inGame
    }
  }

  private fun findPlayers() {
    vPlayer1.adapter = ArrayAdapter<Player>(this, R.layout.text,
      getNewPlayers("Player 1", Piece.Black))
    vPlayer2.adapter = ArrayAdapter<Player>(this, R.layout.text,
      getNewPlayers("Player 2", Piece.Red))
  }

  private fun setupBoard() {
    (0 until  board.numOfCols()).forEach { col ->
      val vColumn = vBoard.addColumn()
      (0 until board.numOfRows()).forEach { row ->
        vColumn.addCell().setOnClickListener {
          board.whosTurn()?.apply {
            val msg = board.tryMakeMove(col, piece)
            if (msg != null) {
              Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
            }
          }
        }
      }
    }

    board.onMoveMade { row, col, piece ->
      val vColumn = vBoard.getChildAt(col) as ViewGroup
      val vRow = vColumn.getChildAt(row) as ImageView
      vRow.setImageResource(when(piece) {
        Piece.Black -> R.drawable.cell_black
        else -> R.drawable.cell_red
      })
      vInfo.setText(when (board.whosTurn()?.piece) {
        Piece.Black -> R.string.blacks_turn
        else -> R.string.reds_turn
      })
    }

    board.onWin { winners ->
      val msgId = when(winners.firstOrNull()?.piece) {
        Piece.Black -> R.string.black_won
        Piece.Red -> R.string.red_won
        else -> R.string.game_is_a_draw
      }

      vInfo.setText(msgId)
    }

    board.onReset {
      vBoard.getChildren<ViewGroup>()
        .flatMap { it.getChildren<ImageView>() }
        .forEach { it.setImageResource(R.drawable.cell_empty) }
    }
  }

  private fun ViewGroup.addColumn(): ViewGroup {
    val vColumn = layoutInflater.inflate(
      R.layout.board_column, this, false
    ) as LinearLayout

    this.addView(vColumn)
    return vColumn
  }

  private fun ViewGroup.addCell(): View {
    val vImageView = layoutInflater.inflate(
      R.layout.board_cell, this, false
    ) as ImageView

    vImageView.setImageResource(R.drawable.cell_empty)
    this.addView(vImageView)
    return vImageView
  }

  @Suppress("UNCHECKED_CAST")
  private fun <T : View> ViewGroup.getChildren(): List<T> {
    return (0 until childCount).map { this.getChildAt(it) as T }
  }
}