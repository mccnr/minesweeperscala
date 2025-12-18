package htwg.minesweeperse.util.state

import htwg.minesweeperse.controller.api.IController

trait GameState:
  def processMove(r: Int, c: Int, controller: IController): Unit //ControllerResult
  def name: String
