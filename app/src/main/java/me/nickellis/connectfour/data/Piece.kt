package me.nickellis.connectfour.data

import android.support.annotation.DrawableRes
import me.nickellis.connectfour.R

enum class Piece(@DrawableRes val drawableId: Int) {
  Empty(R.drawable.cell_empty),
  Black(R.drawable.cell_black),
  Red(R.drawable.cell_red)
}