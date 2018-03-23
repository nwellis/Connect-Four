package me.nickellis.connectfour


class Human(val name: String) : Player {
  override fun isOrganic(): Boolean = true
  override fun toString(): String = name
}