package htwg.minesweeperse.util.state

import htwg.minesweeperse.controller.{GameController, ControllerResult}
import ControllerResult._

class   WinState extends GameState:
  override def name: String = "Win"

  override def processMove(r: Int, c: Int, controller: GameController): Unit =
    controller.lastResult = Win
    //controller.playing = false