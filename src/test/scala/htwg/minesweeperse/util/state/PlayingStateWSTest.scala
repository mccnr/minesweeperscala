package htwg.minesweeperse.util.state

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

import htwg.minesweeperse.util.state.ControllerResult.*
import htwg.minesweeperse.controllerComponent.impl.implGC
import htwg.minesweeperse.model.cell.Cell
import htwg.minesweeperse.model.fieldComponent.impl.{IField, implFieldAdvanced}
import htwg.minesweeperse.util.strategy.revealComponent.impl.StandardRevealStrategy
import htwg.minesweeperse.model.fileIoComponent.IFileIO

class PlayingStateWSTest extends AnyWordSpec {
  
  private object DummyFileIO extends IFileIO {
    override def save(field: IField, seconds: Int): Unit = ()
    override def load(): (IField, Int) =
      (new implFieldAdvanced(1, 1, Vector(Vector(Cell(0)))), 0)
  }
  
  // Hilfsfunktion
  def controllerFromCells(cells: Vector[Vector[Cell]]) =
    new implGC(
      new implFieldAdvanced(cells.length, cells.head.length, cells),
      new StandardRevealStrategy,
      DummyFileIO
    )

  // Tests
  "A PlayingState" should {

    "have the correct name" in {
      PlayingState().name shouldBe "Playing"
    }

    "return OutOfBounds when move is outside field" in {
      val controller = controllerFromCells(
        Vector(
          Vector(Cell(0), Cell(0)),
          Vector(Cell(0), Cell(0))
        )
      )

      val state = PlayingState()
      state.processMove(5, 5, controller)

      controller.lastResult shouldBe OutOfBounds
    }

    "perform a normal reveal using the strategy" in {
      val controller = controllerFromCells(
        Vector(
          Vector(Cell(0), Cell(0)),
          Vector(Cell(1), Cell(0))
        )
      )

      val state = PlayingState()
      state.processMove(0, 0, controller)

      controller.lastResult shouldBe Revealed
      controller.field.isRevealed(0, 0) shouldBe true
    }

    "switch to GameOverState when a mine is revealed" in {
      val controller = controllerFromCells(
        Vector(
          Vector(Cell(1), Cell(0)),
          Vector(Cell(0), Cell(0))
        )
      )

      val state = PlayingState()
      state.processMove(0, 0, controller)

      controller.lastResult shouldBe GameOver
      controller.state.isInstanceOf[GameOverState] shouldBe true
    }

    "switch to WinState when all non-mine cells are revealed" in {
      val controller = controllerFromCells(
        Vector(
          Vector(Cell(0), Cell(1))
        )
      )

      val state = PlayingState()
      state.processMove(0, 0, controller)

      controller.lastResult shouldBe Win
      controller.state.isInstanceOf[WinState] shouldBe true
    }
  }
}