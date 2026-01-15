package htwg.minesweeperse.util.command

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

import htwg.minesweeperse.controllerComponent.impl.implGC
import htwg.minesweeperse.model.cell.Cell
import htwg.minesweeperse.model.fieldComponent.impl.{IField, implFieldAdvanced}
import htwg.minesweeperse.util.strategy.revealComponent.impl.StandardRevealStrategy
import htwg.minesweeperse.util.state._
import htwg.minesweeperse.util.state.ControllerResult._

import htwg.minesweeperse.model.fileIoComponent.IFileIO

class CommandWSTest extends AnyWordSpec {
  
  private object DummyFileIO extends IFileIO {
    override def save(field: IField, seconds: Int): Unit = ()
    override def load(): (IField, Int) = (field2x2Empty(), 0)
  }

  // Hilfsfunktionen
  def field2x2Empty(): IField = {
    val cells = Vector(
      Vector(Cell(0), Cell(0)),
      Vector(Cell(0), Cell(0))
    )
    new implFieldAdvanced(2, 2, cells)
  }

  def fieldWithMine(): IField = {
    val cells = Vector(
      Vector(Cell(1), Cell(0)),
      Vector(Cell(0), Cell(0))
    )
    new implFieldAdvanced(2, 2, cells)
  }

  // Tests
  "A RevealCommand with Undo/Redo" should {

    "execute a reveal via doStep()" in {
      val controller = new implGC(field2x2Empty(), new StandardRevealStrategy, DummyFileIO)

      val cmd = new RevealCommand(controller, 0, 0)
      cmd.doStep()

      controller.field.isRevealed(0, 0) shouldBe true
    }

    "undo a reveal via undoStep()" in {
      val controller = new implGC(field2x2Empty(), new StandardRevealStrategy, DummyFileIO)

      val cmd = new RevealCommand(controller, 0, 0)
      cmd.doStep()
      cmd.undoStep()

      controller.field.isRevealed(0, 0) shouldBe false
      controller.state.isInstanceOf[PlayingState] shouldBe true
    }

    "redo a reveal via redoStep()" in {
      val controller = new implGC(field2x2Empty(), new StandardRevealStrategy, DummyFileIO)

      val cmd = new RevealCommand(controller, 0, 0)
      cmd.doStep()
      cmd.undoStep()
      cmd.redoStep()

      controller.field.isRevealed(0, 0) shouldBe true
    }

    "work through GameController.undo() and redo()" in {
      val controller = new implGC(field2x2Empty(), new StandardRevealStrategy, DummyFileIO)

      controller.processMove(0, 0)
      controller.field.isRevealed(0, 0) shouldBe true

      controller.undo()
      controller.field.isRevealed(0, 0) shouldBe false

      controller.redo()
      controller.field.isRevealed(0, 0) shouldBe true
    }

    "allow undo after GameOver and correctly restore playability" in {
      val controller = new implGC(fieldWithMine(), new StandardRevealStrategy, DummyFileIO)

      // Mine treffen
      controller.processMove(0, 0)
      controller.state.isInstanceOf[GameOverState] shouldBe true

      // Undo zu wieder spielbar
      controller.undo()
      controller.state.isInstanceOf[PlayingState] shouldBe true

      // Weiter spielen
      controller.processMove(1, 1)
      controller.lastResult shouldBe Revealed
    }
  }
}