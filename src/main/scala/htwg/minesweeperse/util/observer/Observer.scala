package htwg.minesweeperse.util.observer

import scala.collection.mutable.ListBuffer

trait Observer:
  def update(): Unit

trait Observable:
  private val observers = ListBuffer.empty[Observer]

  def addObserver(observer: Observer): Unit =
    observers += observer

  def removeObserver(observer: Observer): Unit =
    observers -= observer

  def notifyObservers(): Unit =
    observers.foreach(_.update())
