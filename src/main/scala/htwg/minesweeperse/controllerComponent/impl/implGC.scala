package htwg.minesweeperse.controllerComponent.impl

import com.google.inject.Inject
import htwg.minesweeperse.controllerComponent.impl.IController
import htwg.minesweeperse.util.state.ControllerResult._
import htwg.minesweeperse.model.fieldComponent.impl.IField
import htwg.minesweeperse.util.state.*
import htwg.minesweeperse.util.command.*
import htwg.minesweeperse.util.observer.Observable
import com.google.inject.name.Named
import htwg.minesweeperse.util.strategy.revealComponent.impl.IRevealStrategy

import scala.util.{Try, Success, Failure}

class implGC @Inject() (
   @Named("small") private var _field: IField,
   @Named("standard") private val _revealStrategy: IRevealStrategy
  ) extends Observable, IController {

  private val undoManager = UndoManager()
  private var _lastResult: ControllerResult = Revealed
  private var _state: GameState = PlayingState()

  override def field: IField = _field
  override def field_=(f: IField): Unit = _field = f

  override def revealStrategy: IRevealStrategy = _revealStrategy

  override def lastResult: ControllerResult = _lastResult
  override def lastResult_=(r: ControllerResult): Unit = _lastResult = r

  override def state: GameState = _state
  override def state_=(s: GameState): Unit = _state = s

  override def processMove(r: Int, c: Int): Unit =
    Try {
      undoManager.doStep(RevealCommand(this, r, c))
      _state.processMove(r, c, this)
    } match
      case Success(_) => notifyObservers()
      case Failure(_) =>
        _lastResult = OutOfBounds
        notifyObservers()

  override def undo(): Unit =
    undoManager.undo()
    notifyObservers()

  override def redo(): Unit =
    undoManager.redo()
    notifyObservers()

  override def changeState(state: GameState): Unit =
    this.state = state
}