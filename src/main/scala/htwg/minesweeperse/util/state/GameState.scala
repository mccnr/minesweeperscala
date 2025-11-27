package htwg.minesweeperse.util.state

import htwg.minesweeperse.model.Field
import htwg.minesweeperse.controller.GameController

trait GameState:
  def processMove(r: Int, c: Int, controller: GameController): Unit
  def name: String
