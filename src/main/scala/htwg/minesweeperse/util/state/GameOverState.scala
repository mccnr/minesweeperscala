package htwg.minesweeperse.util.state

import htwg.minesweeperse.controller.ControllerResult
import ControllerResult._
import htwg.minesweeperse.controller.api.IController

class GameOverState extends GameState:
  override def name: String = "GameOver"

  override def processMove(r: Int, c: Int, controller: IController): Unit =
    controller.lastResult = GameOver