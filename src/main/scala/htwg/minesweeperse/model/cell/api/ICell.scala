package htwg.minesweeperse.model.cell.api

trait ICell:

  def isMine: Boolean
  def display(minesAround: Option[Int] = None): String
  def reveal(): ICell
  def isRevealed: Boolean

