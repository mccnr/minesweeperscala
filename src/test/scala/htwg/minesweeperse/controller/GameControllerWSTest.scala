package htwg.minesweeperse.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*
import htwg.minesweeperse.model.*
import htwg.minesweeperse.util.strategy.StandardRevealStrategy
import htwg.minesweeperse.controller.{GameController, ControllerResult}
import ControllerResult.*
import htwg.minesweeperse.util.strategy.RevealStrategy

class GameControllerWSTest extends AnyWordSpec {

  class ThrowingStrategy extends RevealStrategy:
    override def reveal(field: Field, r: Int, c: Int): Field =
      throw new IndexOutOfBoundsException("index out of bounds")

  "A GameController" should {

    "update the field when a move is made" in {
      val field = Field(3, 3, Vector(
        Vector(Cell(0), Cell(0), Cell(0)),
        Vector(Cell(0), Cell(0), Cell(0)),
        Vector(Cell(0), Cell(0), Cell(1))
      ))

      val controller = new GameController(field, StandardRevealStrategy())

      controller.processMove(1, 1)

      controller.lastResult shouldBe Revealed
      controller.field.cells(1)(1).revealed shouldBe true
    }

    "end the game when a mine is revealed" in {
      val cells = Vector(
        Vector(Cell(1, false), Cell(0, false)),
        Vector(Cell(0, false), Cell(0, false))
      )
      val controller = new GameController(Field(2, 2, cells), StandardRevealStrategy())

      controller.processMove(0, 0)

      controller.lastResult shouldBe GameOver
      //controller.playing shouldBe false
    }

    "return OutOfBounds when coordinates are invalid" in {
      val field = Field(3, 3, Vector.fill(3, 3)(Cell(0, false)))
      val controller = new GameController(field, StandardRevealStrategy())

      controller.processMove(5, 5)

      controller.lastResult shouldBe OutOfBounds
      controller.playing shouldBe true
    }
    "set lastResult to OutOfBounds when try fails" in {
      // Spielfeld
      val field = Field(2, 2, Vector.fill(2)(Vector.fill(2)(Cell(0))))
      val controller = new GameController(field, new ThrowingStrategy())

      // Out of Bounds
      val result = controller.processMove(99, 99)

      result shouldBe ControllerResult.OutOfBounds
      controller.lastResult shouldBe ControllerResult.OutOfBounds
    }
  }
}