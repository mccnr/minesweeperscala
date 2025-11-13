package controller

import model._
import util._

class GameController(var field: Field) extends Observable:
  var playing = true

  def processMove(r: Int, c: Int): Unit =
    if r >= 0 && r < field.rows && c >= 0 && c < field.cols then
      field = field.reveal(r, c)
      notifyObservers()
      if field.isWin then
        println("Glückwunsch, du hast alle Minen gefunden!")
        playing = false
      else if field.cells.flatten.exists(c => c.isMine && c.revealed) then
        println("Game Over.")
        playing = false
    else
      println("Koordinate ist außerhalb des Felds.")