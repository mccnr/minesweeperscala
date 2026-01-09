package htwg.minesweeperse.util.state

import htwg.minesweeperse.controllerComponent.impl.IController

trait GameState:
  def processMove(r: Int, c: Int, controller: IController): Unit //ControllerResult
  def name: String
