package htwg.minesweeperse.controller

import htwg.minesweeperse.model._
import htwg.minesweeperse.util.Observable
import htwg.minesweeperse.util.state._
import htwg.minesweeperse.util.strategy._
import ControllerResult._

class GameController(
                      var field: Field,
                      var revealStrategy: RevealStrategy
                    ) extends Observable:

  var playing: Boolean = true
  var lastResult: ControllerResult = Revealed

  var state: GameState = PlayingState()

  def changeState(newState: GameState): Unit =
    this.state = newState

  def processMove(r: Int, c: Int): ControllerResult =
    state.processMove(r, c, this)
    lastResult
