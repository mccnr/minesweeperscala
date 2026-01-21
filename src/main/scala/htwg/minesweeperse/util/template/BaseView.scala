package htwg.minesweeperse.util.template

import htwg.minesweeperse.controllerComponent.impl.IController
import htwg.minesweeperse.util.command.*
import htwg.minesweeperse.util.state.{ControllerResult, GameOverState, PlayingState, WinState}
import scalafx.application.Platform

abstract class BaseView(controller: IController):

  final def start(): Unit =
    showWelcome()
    showField()

    val inputThread = startInputThread()


    // Docker Funktionalität gewährleisten
    if blockOnInputThread then
      inputThread.join()

  private def startInputThread(): Thread =
    val t = new Thread(() =>
      while PlayingState().playing do
        val raw = readInput()

        if raw == null then
          PlayingState().playing = false
        else if raw.trim.isEmpty then
          PlayingState().playing = false
        else
          handleInput(raw)
    )

    t.start()
    t

  private def handleInput(raw: String): Unit =
    parseInput(raw) match
      case None =>
        handleInvalidInput(raw)

      case Some(Move(r, c)) if controller.state.isInstanceOf[GameOverState] =>
        showField()

      case Some(Move(r, c)) if controller.state.isInstanceOf[WinState] =>
        showField()

      case Some(Move(r, c)) =>
        controller.processMove(r, c)

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
        PlayingState().playing = false
        Platform.exit()

      case Some(RestartCmd) =>
        controller.restart()

  // Steps
  def showWelcome(): Unit
  def showField(): Unit
  def readInput(): String
  def parseInput(s: String): Option[InputCommand]
  def handleInvalidInput(s: String): Unit
  def handleResult(result: ControllerResult): Unit
  def update(): Unit
  def blockOnInputThread: Boolean = true