package htwg.minesweeperse.util.template

import htwg.minesweeperse.controllerComponent.impl.IController
import htwg.minesweeperse.util.command.{ExitCmd, InputCommand, InvalidCmd, LoadCmd, Move, RedoCmd, SaveCmd, UndoCmd, RestartCmd}
import htwg.minesweeperse.util.state.{ControllerResult, GameOverState, PlayingState, WinState}
import scalafx.application.Platform

abstract class BaseView(controller: IController):

  final def start(): Unit =
    showWelcome()
    showField()
    startInputThread()
  
  private def startInputThread(): Unit =
    new Thread(() =>
      while PlayingState().playing do
        val raw = readInput()
        if raw.trim.isEmpty then
          PlayingState().playing = false
        else
          handleInput(raw)
    ).start()

  // Verarbeitet einen eingegebenen Befehl
  private def handleInput(raw: String): Unit =
    parseInput(raw) match
      case None =>
        handleInvalidInput(raw)

      case Some(Move(r, c)) if controller.state.isInstanceOf[GameOverState] =>
        showField()

      case Some(Move(r, c)) if controller.state.isInstanceOf[WinState] =>
        showField()

      case Some(Move(r, c)) =>
        val res = controller.processMove(r, c)

      case Some(UndoCmd) =>
        controller.undo()

      case Some(RedoCmd) =>
        controller.redo()

      case Some(SaveCmd) =>
        controller.save()

      case Some(LoadCmd) =>
        controller.load()

      case Some(InvalidCmd) =>
        handleInvalidInput(raw)

      case Some(ExitCmd) =>
        Platform.exit()

      case Some(RestartCmd) =>
        controller.restart()
        //resetTimer()
        //refreshMineCounter()

  // Schritte
  def showWelcome(): Unit
  def showField(): Unit
  def readInput(): String
  def parseInput(s: String): Option[InputCommand]
  def handleInvalidInput(s: String): Unit
  def handleResult(result: ControllerResult): Unit
  def update(): Unit
