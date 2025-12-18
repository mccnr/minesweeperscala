/*package htwg.minesweeperse.controller

import htwg.minesweeperse.controller.ControllerResult
import htwg.minesweeperse.controller.ControllerResult.*
import htwg.minesweeperse.controller.api.IController
import htwg.minesweeperse.model.field.api.IField
import htwg.minesweeperse.util.command.*
import htwg.minesweeperse.util.observer.{Observable, Observer}
import htwg.minesweeperse.util.state.*
import htwg.minesweeperse.util.strategy.reveal.api.IRevealStrategy

import scala.util.{Failure, Success, Try}

class GameController(
   private var _field: IField,
   private val _revealStrategy: IRevealStrategy
   ) extends Observable, IController:

  val undoManager = UndoManager()
  var lastResult: ControllerResult = Revealed
  var state: GameState = PlayingState()

  def changeState(newState: GameState): Unit =
    this.state = newState

  def processMove(r: Int, c: Int): Unit /*ControllerResult*/ =
    Try {
      val cmd = RevealCommand(this, r, c)
      undoManager.doStep(cmd)
      state.processMove(r, c, this)
    } match
      case Success(_) =>
        notifyObservers() // TUI/GUI Parallele, richtige Ausgabe
        lastResult

      case Failure(_) =>
        lastResult = ControllerResult.OutOfBounds
        notifyObservers() // TUI/GUI Parallele, richtige Ausgabe
        lastResult

  def undo(): Unit =
    undoManager.undo()
    notifyObservers()

  def redo(): Unit =
    undoManager.redo()
    notifyObservers()

  override def addObserver(o: Observer): Unit = super.addObserver(o)
  override def removeObserver(o: Observer): Unit = super.removeObserver(o)

  override def field: IField = _field

  override def field_=(f: IField): Unit =
    _field = f

  override def revealStrategy: IRevealStrategy = _revealStrategy */