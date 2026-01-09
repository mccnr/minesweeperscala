package htwg.minesweeperse.util.state

import ControllerResult._
import htwg.minesweeperse.controllerComponent.impl.IController

class   WinState extends GameState:
  override def name: String = "Win"

  override def processMove(r: Int, c: Int, controller: IController): Unit =
    controller.lastResult = Win