package htwg.minesweeperse.util.state

import ControllerResult._
import htwg.minesweeperse.controllerComponent.impl.IController

class GameOverState extends GameState:
  override def name: String = "GameOver"

  override def processMove(r: Int, c: Int, controller: IController): Unit =
    controller.lastResult = GameOver