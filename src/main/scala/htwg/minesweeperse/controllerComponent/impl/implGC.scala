package htwg.minesweeperse.controllerComponent.impl

import com.google.inject.Inject
import htwg.minesweeperse.controllerComponent.impl.IController
import htwg.minesweeperse.util.state.ControllerResult.*
import htwg.minesweeperse.model.fieldComponent.impl.IField
import htwg.minesweeperse.util.state.*
import htwg.minesweeperse.util.command.*
import htwg.minesweeperse.util.observer.Observable
import com.google.inject.name.Named
import htwg.minesweeperse.model.fileIoComponent.IFileIO
import htwg.minesweeperse.util.strategy.revealComponent.impl.IRevealStrategy

import scala.util.{Failure, Success, Try}

class implGC @Inject() (
   @Named("medium") private var _field: IField,
   @Named("standard") private val _revealStrategy: IRevealStrategy,
   @Named("json") private val fileIO: IFileIO // File IO
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
      syncStateWithField()
    } match
      case Success(_) => notifyObservers()
      case Failure(_) =>
        _lastResult = OutOfBounds
        notifyObservers()

  override def undo(): Unit =
    undoManager.undo()
    syncStateWithField()
    notifyObservers()

  override def redo(): Unit =
    undoManager.redo()
    syncStateWithField()
    notifyObservers()

  override def changeState(state: GameState): Unit =
    this.state = state

  override def save(): Unit =
    fileIO.save(field, timerSeconds)

  override def load(): Unit =
    val (newField, seconds) = fileIO.load()
    field = newField
    timerSeconds = seconds
    notifyObservers()

    if field.hasRevealedMine then
      state = GameOverState()
      lastResult = ControllerResult.GameOver

    else if field.isWin then
      autoFlagAllMines()
      state = WinState()
      lastResult = ControllerResult.Win

    else
      state = PlayingState()
      lastResult = ControllerResult.Revealed

    notifyObservers()

  override def syncStateWithField(): Unit =
    if field.hasRevealedMine then
      state = GameOverState()
      lastResult = ControllerResult.GameOver
    else if field.isWin then
      autoFlagAllMines()
      state = WinState()
      lastResult = ControllerResult.Win
    else
      state = PlayingState()

  // Flag test
  override def toggleFlag(r: Int, c: Int): Unit =
    field = field.toggleFlag(r, c)
    notifyObservers()

  // Timer speichern
  private var _timerSeconds: Int = 0
  override def timerSeconds: Int = _timerSeconds
  override def timerSeconds_=(s: Int): Unit = _timerSeconds = s

  // Reset
  override def restart(): Unit =
    // neues Spielfeld erzeugen (gleiche Größe wie vorher)
    val rows = field.rows
    val cols = field.cols

    field = new htwg.minesweeperse.model.fieldComponent.impl.implFieldBase(rows, cols)

    // Undo/Redo zurücksetzen
    undoManager.clear()

    // State,  Result reset
    state = PlayingState()
    lastResult = ControllerResult.Revealed
    PlayingState().playing = true

    // Timer reset
    timerSeconds = 0
    notifyObservers()

  override def autoFlagAllMines(): Unit =
    for r <- 0 until field.rows do
      for c <- 0 until field.cols do
        if field.isMine(r, c) && !field.isFlagged(r, c) then
          field = field.toggleFlag(r, c)
}