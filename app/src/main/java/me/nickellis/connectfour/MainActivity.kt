package me.nickellis.connectfour

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_main.*
import me.nickellis.connectfour.data.Board
import java.util.*

class MainActivity : AppCompatActivity() {

  private val board = Board()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    (0 until  board.numOfCols()).forEach {
      val vColumn = vBoard.addColumn()
      (0 until board.numOfRows()).forEach {
        vColumn.addCell()
      }
    }
  }

  fun ViewGroup.addColumn(): ViewGroup {
    val vColumn = layoutInflater.inflate(
      R.layout.board_column, this, false
    ) as LinearLayout

    this.addView(vColumn)
    return vColumn
  }

  fun ViewGroup.addCell(): View {
    val vImageView = layoutInflater.inflate(
      R.layout.board_cell, this, false
    ) as ImageView

    vImageView.setBackgroundResource(
      when (Random().nextInt(3)) {
        1 -> R.drawable.cell_black
        2 -> R.drawable.cell_red
        else -> R.drawable.cell_empty
      }
    )

    this.addView(vImageView)
    return vImageView
  }
}
