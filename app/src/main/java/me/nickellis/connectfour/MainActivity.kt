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
      if (Random().nextInt(1) == 0) R.drawable.cell_black
      else R.drawable.cell_red
    )

    val rnd = Random()
    val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    vImageView.setBackgroundColor(color)

    this.addView(vImageView)
    return vImageView
  }
}
