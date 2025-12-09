package htwg.minesweeperse.controller

import htwg.minesweeperse.model._
import htwg.minesweeperse.util.Observable
import htwg.minesweeperse.util.state._
import htwg.minesweeperse.util.strategy._
import htwg.minesweeperse.util.command._
import ControllerResult._
import scala.util.{Try, Success, Failure}

class GameController(
 var field: Field,
 var revealStrategy: RevealStrategy
 ) extends Observable:

  val undoManager = UndoManager()

  var playing: Boolean = true
  var lastResult: ControllerResult = Revealed
  var state: GameState = PlayingState()

  def changeState(newState: GameState): Unit =
    this.state = newState

  def processMove(r: Int, c: Int): ControllerResult =
    Try {
      val cmd = RevealCommand(this, r, c)
      undoManager.doStep(cmd)
      state.processMove(r, c, this)
    } match
      case Success(_) =>
        lastResult

      case Failure(_) =>
        lastResult = ControllerResult.OutOfBounds
        lastResult

  def undo(): Unit =
    undoManager.undo()

  def redo(): Unit =
    undoManager.redo()
