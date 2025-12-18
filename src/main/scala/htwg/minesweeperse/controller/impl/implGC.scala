package htwg.minesweeperse.controller.impl

import htwg.minesweeperse.controller.api.IController
import htwg.minesweeperse.controller.ControllerResult
import htwg.minesweeperse.controller.ControllerResult.*
import htwg.minesweeperse.model.field.api.IField
import htwg.minesweeperse.util.command.*
import htwg.minesweeperse.util.observer.{Observable, Observer}
import htwg.minesweeperse.util.state.*
import htwg.minesweeperse.util.strategy.reveal.api.IRevealStrategy

import scala.util.{Failure, Success, Try}

// IController impl GC
class implGC(
  private var _field: IField,
  private val _revealStrategy: IRevealStrategy
  ) extends Observable, IController:

  val undoManager = UndoManager()
  private var _lastResult: ControllerResult = Revealed
  private var _state: GameState = PlayingState()

  override def changeState(newState: GameState): Unit =
    this.state = newState

  override def processMove(r: Int, c: Int): Unit =
    Try {
      val cmd = RevealCommand(this, r, c)
      undoManager.doStep(cmd)
      _state.processMove(r, c, this)
    } match
      case Success(_) => notifyObservers() // TUI/GUI Parallele, richtige Ausgabe
      case Failure(_) =>
        _lastResult = OutOfBounds
        notifyObservers() // TUI/GUI Parallele, richtige Ausgabe

  override def undo(): Unit =
    undoManager.undo()
    notifyObservers()

  override def redo(): Unit =
    undoManager.redo()
    notifyObservers()

  override def field: IField = _field

  override def field_=(f: IField): Unit = _field = f

  override def revealStrategy: IRevealStrategy = _revealStrategy

  override def lastResult: ControllerResult = _lastResult

  override def lastResult_=(r: ControllerResult): Unit = _lastResult = r

  override def state: GameState = _state

  override def state_=(s: GameState): Unit = _state = s