package htwg.minesweeperse.util.command

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*
import htwg.minesweeperse.controller.*
import htwg.minesweeperse.model.*
import htwg.minesweeperse.util.strategy.StandardRevealStrategy
import htwg.minesweeperse.util.state.*

class CommandWSTest extends AnyWordSpec {

  "A RevealCommand with Undo/Redo" should {

    "execute a reveal via doStep()" in {
      val field = Field(2, 2, Vector(
        Vector(Cell(0), Cell(0)),
        Vector(Cell(0), Cell(0))
      ))

      val controller = new GameController(field, StandardRevealStrategy())
      val cmd = new RevealCommand(controller, 0, 0)

      cmd.doStep()

      controller.field.cells(0)(0).revealed shouldBe true
    }

    "undo a reveal via undoStep()" in {
      val field = Field(2, 2, Vector(
        Vector(Cell(0), Cell(0)),
        Vector(Cell(0), Cell(0))
      ))

      val controller = new GameController(field, StandardRevealStrategy())
      val cmd = new RevealCommand(controller, 0, 0)

      cmd.doStep()
      cmd.undoStep()

      controller.field.cells(0)(0).revealed shouldBe false
      controller.state.isInstanceOf[PlayingState] shouldBe true
    }

    "redo a reveal via redoStep()" in {
      val field = Field(2, 2, Vector(
        Vector(Cell(0), Cell(0)),
        Vector(Cell(0), Cell(0))
      ))

      val controller = new GameController(field, StandardRevealStrategy())
      val cmd = new RevealCommand(controller, 0, 0)

      cmd.doStep()
      cmd.undoStep()
      cmd.redoStep()

      controller.field.cells(0)(0).revealed shouldBe true
    }

    "work through GameController.undo() and redo()" in {
      val field = Field(2, 2, Vector.fill(2,2)(Cell(0)))

      val controller = new GameController(field, StandardRevealStrategy())

      controller.processMove(0,0)
      controller.field.cells(0)(0).revealed shouldBe true

      controller.undo()
      controller.field.cells(0)(0).revealed shouldBe false

      controller.redo()
      controller.field.cells(0)(0).revealed shouldBe true
    }

    "allow undo after GameOver and correctly restore playability" in {
      val field = Field(2, 2, Vector(
        Vector(Cell(1), Cell(0)),
        Vector(Cell(0), Cell(0))
      ))

      val controller = new GameController(field, StandardRevealStrategy())

      // Mine treffen, d.h. GameOverState
      controller.processMove(0,0)
      controller.state.isInstanceOf[GameOverState] shouldBe true

      // Undo, d.h. Zustand soll wieder PlayingState sein
      controller.undo()
      controller.state.isInstanceOf[PlayingState] shouldBe true
      PlayingState().playing shouldBe true

      // Jetzt wieder ein normaler Zug sollte erlaubt sein
      controller.processMove(1,1)
      controller.lastResult shouldBe ControllerResult.Revealed
    }
  }
}