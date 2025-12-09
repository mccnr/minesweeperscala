package htwg.minesweeperse.util.template

import htwg.minesweeperse.controller.{ControllerResult, GameController, InputCommand, Move, UndoCmd, RedoCmd, InvalidCmd}
import htwg.minesweeperse.util.state.{GameOverState, WinState}

abstract class BaseView(controller: GameController):

  final def startGameLoop(): Unit =
    showWelcome()
    showField()

    while controller.playing do
      val raw = readInput()
      if raw.isEmpty then
        controller.playing = false
      else
        parseInput(raw) match
          case None =>
            handleInvalidInput(raw)

          case Some(Move(r, c)) if controller.state.isInstanceOf[GameOverState] =>
            showField()

          case Some(Move(r, c)) if controller.state.isInstanceOf[WinState] =>
            showField()

          case Some(Move(r, c)) =>
            val res = controller.processMove(r, c)
            handleResult(res)
            showField()

          case Some(UndoCmd) =>
            controller.undo()
            showField()

          case Some(RedoCmd) =>
            controller.redo()
            showField()

          case Some(InvalidCmd) =>
            handleInvalidInput(raw)

  // Schritte
  def showWelcome(): Unit
  def showField(): Unit
  def readInput(): String
  def parseInput(s: String): Option[InputCommand]
  def handleInvalidInput(s: String): Unit
  def handleResult(result: ControllerResult): Unit
