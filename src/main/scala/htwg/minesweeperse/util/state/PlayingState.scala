package htwg.minesweeperse.util.state

import ControllerResult._
import htwg.minesweeperse.controllerComponent.impl.IController

class PlayingState extends GameState:

  override def name: String = "Playing"
  var playing: Boolean = true

  override def processMove(r: Int, c: Int, controller: IController): Unit =
    val field = controller.field

    // Out of Bounds?
    if r < 0 || r >= field.rows || c < 0 || c >= field.cols then
      controller.lastResult = OutOfBounds
      controller.notifyObservers()
      return

    // Reveal logic von Strategy
    val newField = controller.revealStrategy.reveal(field, r, c)
    controller.field = newField
    controller.notifyObservers()

    // Mine?
    if newField.hasRevealedMine /*newField.cells.flatten.exists(c => c.isMine && c.revealed)*/ then
      controller.lastResult = GameOver
      controller.changeState(new GameOverState)
      controller.state.processMove(r, c, controller) // logik des aktuellen states zu ende verarbeiten
      return

    // Win?
    if newField.isWin then
      controller.lastResult = Win
      controller.changeState(new WinState)
      controller.state.processMove(r, c, controller)
      return

    // Normaler Zug
    controller.lastResult = Revealed
