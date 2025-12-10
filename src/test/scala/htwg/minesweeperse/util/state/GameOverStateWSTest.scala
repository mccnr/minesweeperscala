package htwg.minesweeperse.util.state

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*
import htwg.minesweeperse.controller.*
import htwg.minesweeperse.model.*
import htwg.minesweeperse.util.strategy.StandardRevealStrategy

class GameOverStateWSTest extends AnyWordSpec {

  "The GameOverState" should {

    "return the correct name" in {
      val state = new GameOverState
      state.name shouldBe "GameOver"
    }

    "set lastResult = GameOver and stop the game when processMove is called" in {

      val field = Field(2, 2, Vector(
        Vector(Cell(0), Cell(0)),
        Vector(Cell(0), Cell(0))
      ))

      val controller = new GameController(field, StandardRevealStrategy())

      PlayingState().playing shouldBe true

      val state = new GameOverState

      state.processMove(0, 0, controller)

      controller.lastResult shouldBe ControllerResult.GameOver
    }
  }
}
