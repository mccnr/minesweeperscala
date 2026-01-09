package htwg.minesweeperse.util.state

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

import htwg.minesweeperse.controllerComponent.impl.implGC
import htwg.minesweeperse.model.cell.Cell
import htwg.minesweeperse.model.fieldComponent.impl.implFieldAdvanced
import htwg.minesweeperse.util.strategy.revealComponent.impl.StandardRevealStrategy
import htwg.minesweeperse.util.state.ControllerResult.*

class WinStateWSTest extends AnyWordSpec {

  /* -----------------------------------
   * Hilfsfunktion
   * ----------------------------------- */

  def controllerWithEmptyField() =
    new implGC(
      new implFieldAdvanced(
        2,
        2,
        Vector(
          Vector(Cell(0), Cell(0)),
          Vector(Cell(0), Cell(0))
        )
      ),
      new StandardRevealStrategy
    )

  /* -----------------------------------
   * Tests
   * ----------------------------------- */

  "A WinState" should {

    "have the correct name" in {
      val state = WinState()
      state.name shouldBe "Win"
    }

    "set lastResult = Win and stop the game when processing a move" in {
      val controller = controllerWithEmptyField()
      val state = WinState()

      // Vorbedingung: Spiel wäre spielbar
      PlayingState().playing shouldBe true

      state.processMove(0, 0, controller)

      controller.lastResult shouldBe Win
    }
  }
}