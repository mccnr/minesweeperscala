package htwg.minesweeperse.util.state

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

import htwg.minesweeperse.controller.ControllerResult.*
import htwg.minesweeperse.controller.api.IController
import htwg.minesweeperse.model.cell.api.ICell
import htwg.minesweeperse.model.field.api.IField

import htwg.minesweeperse.util.factory.cellFactory.CellCreator
import htwg.minesweeperse.util.factory.fieldFactory.RandomFieldCreator
import htwg.minesweeperse.util.factory.controllerFactory.ControllerCreator
import htwg.minesweeperse.util.factory.revealFactory.StandardRevealCreator

class PlayingStateWSTest extends AnyWordSpec {

// Factories
  val cellCreator       = CellCreator()
  val fieldCreator      = RandomFieldCreator()
  val revealCreator     = StandardRevealCreator()
  val controllerCreator = ControllerCreator()

// Hilfsfunktionen
  def emptyCell(): ICell = cellCreator.create(0)
  def mineCell(): ICell  = cellCreator.create(1)

  def controllerFromCells(cells: Vector[Vector[ICell]]): IController = {
    val field  = fieldCreator.fromCells(cells)
    val reveal = revealCreator.create()
    controllerCreator.create(field, reveal)
  }

// Tests
  "A PlayingState" should {

    "have the correct name" in {
      PlayingState().name shouldBe "Playing"
    }

    "return OutOfBounds when move is outside field" in {
      val controller =
        controllerFromCells(
          Vector(
            Vector(emptyCell(), emptyCell()),
            Vector(emptyCell(), emptyCell())
          )
        )

      val state = PlayingState()
      state.processMove(5, 5, controller)

      controller.lastResult shouldBe OutOfBounds
    }

    "perform a normal reveal using the strategy" in {
      val controller =
        controllerFromCells(
          Vector(
            Vector(emptyCell(), emptyCell()),
            Vector(mineCell(),  emptyCell())
          )
        )

      val state = PlayingState()
      state.processMove(0, 0, controller)

      controller.lastResult shouldBe Revealed
      controller.field.isRevealed(0, 0) shouldBe true
    }

    "switch to GameOverState when a mine is revealed" in {
      val controller =
        controllerFromCells(
          Vector(
            Vector(mineCell(), emptyCell()),
            Vector(emptyCell(), emptyCell())
          )
        )

      val state = PlayingState()
      state.processMove(0, 0, controller)

      controller.lastResult shouldBe GameOver
      controller.state.isInstanceOf[GameOverState] shouldBe true
    }

    "switch to WinState when all non-mine cells are revealed" in {
      val controller =
        controllerFromCells(
          Vector(
            Vector(emptyCell(), mineCell())
          )
        )

      val state = PlayingState()
      state.processMove(0, 0, controller)

      controller.lastResult shouldBe Win
      controller.state.isInstanceOf[WinState] shouldBe true
    }
  }
}