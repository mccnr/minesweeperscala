package htwg.minesweeperse.util.state

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*
import htwg.minesweeperse.model.*
import htwg.minesweeperse.controller.*
import htwg.minesweeperse.util.strategy.StandardRevealStrategy

class WinStateWSTest extends AnyWordSpec {

  "A WinState" should {

    "have the correct name" in {
      val state = WinState()
      state.name shouldBe "Win"
    }

    "set lastResult = Win and stop the game when processing a move" in {
      val field = Field(2, 2, Vector(
        Vector(Cell(0), Cell(0)),
        Vector(Cell(0), Cell(0))
      ))

      val controller = new GameController(field, StandardRevealStrategy())
      val state = WinState()

      controller.playing shouldBe true

      state.processMove(0, 0, controller)

      controller.lastResult shouldBe ControllerResult.Win
      //controller.playing shouldBe false
    }
  }
}
