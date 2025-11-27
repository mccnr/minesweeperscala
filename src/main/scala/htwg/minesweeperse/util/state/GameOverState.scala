package htwg.minesweeperse.util.state

import htwg.minesweeperse.controller.{GameController, ControllerResult}
import ControllerResult.*

class GameOverState extends GameState:
  override def name: String = "GameOver"

  override def processMove(r: Int, c: Int, controller: GameController): Unit =
    controller.lastResult = GameOver
    controller.playing = false;