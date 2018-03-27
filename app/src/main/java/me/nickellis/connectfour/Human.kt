package me.nickellis.connectfour

import me.nickellis.connectfour.data.Piece

class Human(val name: String, piece: Piece) : Player(piece, false) {
  override fun toString(): String = name
}