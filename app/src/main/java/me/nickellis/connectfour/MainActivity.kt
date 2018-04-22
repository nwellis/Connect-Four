package me.nickellis.connectfour

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import icepick.State
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import me.nickellis.connectfour.ai.AI
import me.nickellis.connectfour.data.Board
import me.nickellis.connectfour.data.Piece

class MainActivity : AppCompatActivity() {

  @State @JvmField var inGame = false
  private val board = Board()
  private val tasks: MutableList<Job> = mutableListOf()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    setupBoard()
    findPlayers()

    // Initialize the pieces
    if (savedInstanceState == null) {
      board.reset()
      vAction.setText(R.string.begin)
      vInfo.setText(R.string.start_the_game)
    }

    vAction.setOnClickListener {
      if (inGame) {
        resetGame()
      } else {
        startGame()
      }
      inGame = !inGame
    }
  }

  private fun startGame() {
    board.addPlayer(vPlayer1.selectedItem as Player)
    board.addPlayer(vPlayer2.selectedItem as Player)
    vAction.setText(R.string.reset)
    vInfo.setText(when (board.whosTurn()?.piece) {
      Piece.Black -> R.string.blacks_turn
      else -> R.string.reds_turn
    })

    val player = board.whosTurn()
    if (player is AI) {
      tasks.add(launch(UI) {
        makeAIMove(player, player.piece)
      })
    }
  }

  private fun resetGame() {
    board.reset()
    vAction.setText(R.string.begin)
    vInfo.setText(R.string.start_the_game)
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
          if (!inGame) {
            startGame()
            inGame = true
          }
          board.whosTurn()?.apply {
            if (this is AI) return@apply
            val msg = board.tryMakeMove(col, piece)
            if (msg != null) {
              Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
            }
          }
        }
      }
    }

    board.onMoveMade { col, row, piece ->
      val vColumn = vBoard.getChildAt(col) as ViewGroup
      val vRow = vColumn.getChildAt(vColumn.childCount - row - 1) as ImageView
      vRow.setImageResource(piece.drawableId)

      // Wait for user input, or run AI move
      val player = board.whosTurn()
      when {
        player is AI -> {
          tasks.add(launch(UI) {
            makeAIMove(player, player.piece)
          })
        }
        player?.piece == Piece.Black -> vInfo.setText(R.string.blacks_turn)
        player?.piece == Piece.Red -> vInfo.setText(R.string.reds_turn)
      }
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
        .forEach { it.setImageResource(Piece.Empty.drawableId) }
    }
  }

  private suspend fun makeAIMove(r2d2: AI, piece: Piece) {
    vInfo.text = resources.getString(R.string.s_is_thinking, r2d2.toString())
    val move = async { r2d2.makeMove(board) }.await()
    val msg = board.tryMakeMove(move, piece)
    if (msg != null) {
      val error = "$r2d2 made an invalid move on column $move: $msg"
      Log.e("MainActivity", error)
      if (BuildConfig.DEBUG) {
        vInfo.text = error
      }
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

    vImageView.setImageResource(Piece.Empty.drawableId)
    this.addView(vImageView)
    return vImageView
  }

  @Suppress("UNCHECKED_CAST")
  private fun <T : View> ViewGroup.getChildren(): List<T> {
    return (0 until childCount).map { this.getChildAt(it) as T }
  }
}
