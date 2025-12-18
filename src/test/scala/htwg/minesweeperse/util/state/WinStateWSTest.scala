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

class WinStateWSTest extends AnyWordSpec {

  // Factories
  val cellFactory       = CellCreator()
  val fieldFactory      = RandomFieldCreator()
  val controllerFactory = ControllerCreator()
  val revealFactory     = StandardRevealCreator()

  // Hilfsfunktionen
  def emptyCell(): ICell = cellFactory.create(0)

  def empty2x2Field(): IField =
    fieldFactory.fromCells(
      Vector(
        Vector(emptyCell(), emptyCell()),
        Vector(emptyCell(), emptyCell())
      )
    )

  def controllerWithEmptyField(): IController =
    controllerFactory.create(
      empty2x2Field(),
      revealFactory.create()
    )

   // Tests
  "A WinState" should {

    "have the correct name" in {
      val state = WinState()
      state.name shouldBe "Win"
    }

    "set lastResult = Win and stop the game when processing a move" in {
      val controller = controllerWithEmptyField()
      val state = WinState()

      // Vorbedingung
      PlayingState().playing shouldBe true

      state.processMove(0, 0, controller)

      controller.lastResult shouldBe ControllerResult.Win
    }
  }
}