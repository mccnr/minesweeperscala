package htwg.minesweeperse.util.command

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

import htwg.minesweeperse.controller.ControllerResult
import htwg.minesweeperse.controller.ControllerResult.*

import htwg.minesweeperse.controller.api.IController
import htwg.minesweeperse.util.factory.controllerFactory.ControllerCreator

import htwg.minesweeperse.model.field.api.IField
import htwg.minesweeperse.util.factory.fieldFactory.RandomFieldCreator

import htwg.minesweeperse.model.cell.api.ICell
import htwg.minesweeperse.util.factory.cellFactory.CellCreator

import htwg.minesweeperse.util.factory.revealFactory.StandardRevealCreator

import htwg.minesweeperse.util.state.*

class CommandWSTest extends AnyWordSpec {

 // Factories
  val cellFactory       = CellCreator()
  val fieldFactory      = RandomFieldCreator()
  val revealCreator     = StandardRevealCreator()
  val controllerCreator = ControllerCreator()

// Hilfsfunktionen
  def emptyCell(): ICell = cellFactory.create(0)
  def mineCell(): ICell  = cellFactory.create(1)

  def field2x2Empty(): IField =
    fieldFactory.fromCells(
      Vector(
        Vector(emptyCell(), emptyCell()),
        Vector(emptyCell(), emptyCell())
      )
    )

  def fieldWithMine(): IField =
    fieldFactory.fromCells(
      Vector(
        Vector(mineCell(), emptyCell()),
        Vector(emptyCell(), emptyCell())
      )
    )

// Tests
  "A RevealCommand with Undo/Redo" should {

    "execute a reveal via doStep()" in {
      val field = field2x2Empty()
      val reveal = revealCreator.create()
      val controller = controllerCreator.create(field, reveal)

      val cmd = new RevealCommand(controller, 0, 0)
      cmd.doStep()

      controller.field.isRevealed(0, 0) shouldBe true
    }

    "undo a reveal via undoStep()" in {
      val field = field2x2Empty()
      val reveal = revealCreator.create()
      val controller = controllerCreator.create(field, reveal)

      val cmd = new RevealCommand(controller, 0, 0)
      cmd.doStep()
      cmd.undoStep()

      controller.field.isRevealed(0, 0) shouldBe false
      controller.state.isInstanceOf[PlayingState] shouldBe true
    }

    "redo a reveal via redoStep()" in {
      val field = field2x2Empty()
      val reveal = revealCreator.create()
      val controller = controllerCreator.create(field, reveal)

      val cmd = new RevealCommand(controller, 0, 0)
      cmd.doStep()
      cmd.undoStep()
      cmd.redoStep()

      controller.field.isRevealed(0, 0) shouldBe true
    }

    "work through GameController.undo() and redo()" in {
      val field = field2x2Empty()
      val reveal = revealCreator.create()
      val controller = controllerCreator.create(field, reveal)

      controller.processMove(0, 0)
      controller.field.isRevealed(0, 0) shouldBe true

      controller.undo()
      controller.field.isRevealed(0, 0) shouldBe false

      controller.redo()
      controller.field.isRevealed(0, 0) shouldBe true
    }

    "allow undo after GameOver and correctly restore playability" in {
      val field = fieldWithMine()
      val reveal = revealCreator.create()
      val controller = controllerCreator.create(field, reveal)

      // Mine treffen
      controller.processMove(0, 0)
      controller.state.isInstanceOf[GameOverState] shouldBe true

      // Undo dann wieder PlayingState
      controller.undo()
      controller.state.isInstanceOf[PlayingState] shouldBe true

      // Weiter spielen erlaubt
      controller.processMove(1, 1)
      controller.lastResult shouldBe Revealed
    }
  }
}
