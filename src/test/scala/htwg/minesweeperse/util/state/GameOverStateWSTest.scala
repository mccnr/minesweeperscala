package htwg.minesweeperse.util.state

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

import htwg.minesweeperse.controller.ControllerResult
import htwg.minesweeperse.controller.api.IController

import htwg.minesweeperse.model.cell.api.ICell
import htwg.minesweeperse.model.field.api.IField

import htwg.minesweeperse.util.factory.cellFactory.CellCreator
import htwg.minesweeperse.util.factory.fieldFactory.RandomFieldCreator
import htwg.minesweeperse.util.factory.controllerFactory.ControllerCreator
import htwg.minesweeperse.util.factory.revealFactory.StandardRevealCreator

class GameOverStateWSTest extends AnyWordSpec {

// Factories
  val cellFactory      = CellCreator()
  val fieldFactory     = RandomFieldCreator()
  val controllerFactory = ControllerCreator()
  val revealCreator    = StandardRevealCreator()

// Hilfsfunktionen
  def emptyCell(): ICell = cellFactory.create(0)

  def field2x2(): IField =
    fieldFactory.fromCells(
      Vector(
        Vector(emptyCell(), emptyCell()),
        Vector(emptyCell(), emptyCell())
      )
    )

// Tests
  "The GameOverState" should {

    "return the correct name" in {
      val state = new GameOverState
      state.name shouldBe "GameOver"
    }

    "set lastResult = GameOver when processMove is called" in {

      val field: IField = field2x2()
      val reveal        = revealCreator.create()
      val controller: IController =
        controllerFactory.create(field, reveal)

      // Vorbedingung
      controller.lastResult should not be ControllerResult.GameOver

      val state = new GameOverState

      state.processMove(0, 0, controller)

      controller.lastResult shouldBe ControllerResult.GameOver
    }
  }
}