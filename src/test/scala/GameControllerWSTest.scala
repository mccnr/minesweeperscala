package controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import model._

class GameControllerWSTest extends AnyWordSpec {

  "A GameController" should {

    "update the field when a valid move is made" in {
      val field = Field(3, 3, Vector.fill(3, 3)(Cell(0, false)))
      val controller = new GameController(field)

      controller.processMove(1, 1)
      controller.field.cells(1)(1).revealed shouldBe true
    }

    "end the game when a mine is revealed" in {
      val cells = Vector(
        Vector(Cell(1, false), Cell(0, false)),
        Vector(Cell(0, false), Cell(0, false))
      )
      val controller = new GameController(Field(2, 2, cells))

      controller.processMove(0, 0)
      controller.playing shouldBe false
    }

    "print error for out of bounds coordinates" in {
      val field = Field(3, 3, Vector.fill(3, 3)(Cell(0, false)))
      val controller = new GameController(field)

      val out = new java.io.ByteArrayOutputStream()
      Console.withOut(new java.io.PrintStream(out)) {
        controller.processMove(5, 5)
      }

      out.toString should include("Koordinate ist außerhalb des Felds.")
    }
  }
}