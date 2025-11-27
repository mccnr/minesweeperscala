package htwg.minesweeperse.util.state

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*
import htwg.minesweeperse.model.*
import htwg.minesweeperse.controller.*
import htwg.minesweeperse.util.strategy.*

class PlayingStateWSTest extends AnyWordSpec {

  "A PlayingState" should {

    "have the correct name" in {
      PlayingState().name shouldBe "Playing"
    }

    "return OutOfBounds when move is outside field" in {
      val field = Field(2, 2, Vector.fill(2, 2)(Cell(0)))
      val controller = new GameController(field, StandardRevealStrategy())

      val state = PlayingState()
      state.processMove(5, 5, controller)

      controller.lastResult shouldBe ControllerResult.OutOfBounds
      controller.field shouldBe field
    }

    "perform a normal reveal using the strategy" in {
      val field = Field(2, 2, Vector(
        Vector(Cell(0), Cell(0)),
        Vector(Cell(1), Cell(0))
      ))

      val controller = new GameController(field, StandardRevealStrategy())
      val state = PlayingState()

      state.processMove(0, 0, controller)

      controller.lastResult shouldBe ControllerResult.Revealed
      controller.field.cells(0)(0).revealed shouldBe true
    }

    "switch to GameOverState when a mine is revealed" in {
      val field = Field(2, 2, Vector(
        Vector(Cell(1), Cell(0)),
        Vector(Cell(0), Cell(0))
      ))

      val controller = new GameController(field, StandardRevealStrategy())
      val state = PlayingState()

      state.processMove(0, 0, controller)

      controller.lastResult shouldBe ControllerResult.GameOver
      controller.state.isInstanceOf[GameOverState] shouldBe true
    }

    "switch to WinState when all non-mine cells are revealed" in {
      val field = Field(1, 2, Vector(
        Vector(Cell(0), Cell(1))
      ))

      val controller = new GameController(field, StandardRevealStrategy())
      val state = PlayingState()

      // reveal the only non mine
      state.processMove(0, 0, controller)

      controller.lastResult shouldBe ControllerResult.Win
      controller.state.isInstanceOf[WinState] shouldBe true
    }
  }
}
