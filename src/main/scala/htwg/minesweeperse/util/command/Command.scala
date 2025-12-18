package htwg.minesweeperse.util.command

trait Command:
  def doStep(): Unit
  def undoStep(): Unit
  def redoStep(): Unit