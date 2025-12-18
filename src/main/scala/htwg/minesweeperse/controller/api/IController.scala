package htwg.minesweeperse.controller.api

import htwg.minesweeperse.model.field.api.IField
import htwg.minesweeperse.controller.ControllerResult
import htwg.minesweeperse.util.observer.Observer
import htwg.minesweeperse.util.state.GameState
import htwg.minesweeperse.util.strategy.reveal.api.IRevealStrategy

trait IController:

  def field: IField
  def field_=(f: IField): Unit

  def revealStrategy: IRevealStrategy

  def lastResult: ControllerResult
  def lastResult_=(r: ControllerResult): Unit

  def state: GameState
  def state_=(s: GameState): Unit

  def processMove(r: Int, c: Int): Unit //ControllerResult
  def undo(): Unit
  def redo(): Unit

  def addObserver(o: Observer): Unit
  def removeObserver(o: Observer): Unit

  def changeState(state: GameState): Unit
  def notifyObservers(): Unit


