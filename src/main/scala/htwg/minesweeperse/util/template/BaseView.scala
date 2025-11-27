package htwg.minesweeperse.util.template

import htwg.minesweeperse.controller.GameController
import htwg.minesweeperse.controller.ControllerResult

abstract class BaseView(controller: GameController):

  final def startGameLoop(): Unit =
    showWelcome()
    showField()

    while controller.playing do
      val input = readInput()
      if input.isEmpty then
        controller.playing = false
      else
        parseInput(input) match
          case None =>
            handleInvalidInput(input)
          case Some((r, c)) =>
            val result = controller.processMove(r, c)
            handleResult(result)
            showField()

  // Schritte
  def showWelcome(): Unit
  def showField(): Unit

  def readInput(): String
  def parseInput(s: String): Option[(Int, Int)]
  def handleInvalidInput(s: String): Unit
  def handleResult(result: ControllerResult): Unit
