package htwg.minesweeperse.controllerComponent.impl

import htwg.minesweeperse.model.fieldComponent.impl.IField
import htwg.minesweeperse.util.observer.Observer
import htwg.minesweeperse.util.state.{ControllerResult, GameState}
import htwg.minesweeperse.util.strategy.revealComponent.impl.IRevealStrategy

trait IController:

  def field: IField // getter
  def field_=(f: IField): Unit // setter

  def revealStrategy: IRevealStrategy

  def lastResult: ControllerResult
  def lastResult_=(r: ControllerResult): Unit

  def state: GameState
  def state_=(s: GameState): Unit

  def processMove(r: Int, c: Int): Unit //ControllerResult
  def undo(): Unit
  def redo(): Unit

  def changeState(state: GameState): Unit
  def notifyObservers(): Unit

  def addObserver(o: Observer): Unit
  def removeObserver(o: Observer): Unit

  def save(): Unit
  def load(): Unit

  def syncStateWithField(): Unit

  def toggleFlag(r: Int, c: Int): Unit // flag test

  // Saving Timer
  def timerSeconds: Int
  def timerSeconds_=(s: Int): Unit

  // Reset
  def restart(): Unit






