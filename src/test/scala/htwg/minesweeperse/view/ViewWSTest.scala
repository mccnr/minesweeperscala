package htwg.minesweeperse.view

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

import java.io.*

import htwg.minesweeperse.controller.*
import htwg.minesweeperse.controller.api.IController

import htwg.minesweeperse.model.field.api.IField
import htwg.minesweeperse.model.cell.api.ICell

import htwg.minesweeperse.util.factory.cellFactory.CellCreator
import htwg.minesweeperse.util.factory.fieldFactory.RandomFieldCreator
import htwg.minesweeperse.util.factory.controllerFactory.ControllerCreator
import htwg.minesweeperse.util.factory.revealFactory.StandardRevealCreator

class ViewWSTest extends AnyWordSpec {

  // Factories
  val cellCreator       = CellCreator()
  val fieldCreator      = RandomFieldCreator()
  val revealCreator     = StandardRevealCreator()
  val controllerCreator = ControllerCreator()

  def emptyCell(): ICell = cellCreator.create(0)
  def mineCell(): ICell  = cellCreator.create(1)

  def fieldFromCells(cells: Vector[Vector[ICell]]): IField =
    fieldCreator.fromCells(cells)

  private def makeView(input: String, field: IField): (GameView, IController, ByteArrayOutputStream) = {
    val in       = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(input.getBytes)))
    val outBytes = new ByteArrayOutputStream()
    val out      = new PrintStream(outBytes)

    val controller = controllerCreator.create(field, revealCreator.create())
    val view       = new GameView(controller, out, in)

    (view, controller, outBytes)
  }

  "GameView" should {

    "process valid numeric input" in {
      val field = fieldFromCells(
        Vector(
          Vector(emptyCell(), emptyCell()),
          Vector(emptyCell(), emptyCell())
        )
      )

      val (view, controller, _) = makeView("1 1\n", field)

      val raw = view.readInput()
      val Some(Move(r, c)) = view.parseInput(raw)

      controller.processMove(r, c)                 // Unit
      view.handleResult(controller.lastResult)     // ControllerResult

      controller.field.isRevealed(0, 0) shouldBe true
    }

    "print error for invalid input" in {
      val field = fieldCreator.create(2, 2)
      val (view, _, bytes) = makeView("abc def\n", field)

      val raw = view.readInput()
      view.parseInput(raw) match
        case None => view.handleInvalidInput(raw)
        case _    => fail("Should be invalid")

      bytes.toString should include ("Bitte zwei Zahlen eingeben")
    }

    "update view when controller notifies observers" in {
      val field = fieldCreator.create(2, 2)
      val (view, controller, bytes) = makeView("", field)

      controller.processMove(0, 0)
      view.update()

      bytes.toString should include ("|")
    }

    "print OutOfBounds when move is outside field" in {
      val field = fieldCreator.create(2, 2)
      val (view, controller, bytes) = makeView("9 9\n", field)

      val raw = view.readInput()
      val Some(Move(r, c)) = view.parseInput(raw)

      controller.processMove(r, c)
      view.handleResult(controller.lastResult)

      bytes.toString should include ("Koordinate ist außerhalb des Felds.")
    }

    "print Game Over when a mine is revealed" in {
      val field = fieldFromCells(
        Vector(
          Vector(mineCell(), emptyCell()),
          Vector(emptyCell(), emptyCell())
        )
      )

      val (view, controller, bytes) = makeView("1 1\n", field)

      val raw = view.readInput()
      val Some(Move(r, c)) = view.parseInput(raw)

      controller.processMove(r, c)
      view.handleResult(controller.lastResult)

      bytes.toString should include ("Game Over.")
    }

    "print Win when all non-mine cells are revealed" in {
      // Hier reicht ein Feld ohne Minen, damit nach erstem Reveal direkt Win kommt
      val field = fieldFromCells(
        Vector(
          Vector(emptyCell(), emptyCell()),
          Vector(emptyCell(), emptyCell())
        )
      )

      val (view, controller, bytes) = makeView("1 1\n", field)

      val raw = view.readInput()
      val Some(Move(r, c)) = view.parseInput(raw)

      controller.processMove(r, c)
      view.handleResult(controller.lastResult)

      bytes.toString should include ("Glückwunsch")
    }

    "parse undo command" in {
      val field = fieldCreator.create(2, 2)
      val (view, _, _) = makeView("", field)

      view.parseInput("undo") shouldBe Some(UndoCmd)
    }

    "parse redo command" in {
      val field = fieldCreator.create(2, 2)
      val (view, _, _) = makeView("", field)

      view.parseInput("redo") shouldBe Some(RedoCmd)
    }

    "parse valid move command" in {
      val field = fieldCreator.create(2, 2)
      val (view, _, _) = makeView("", field)

      view.parseInput("2 2") shouldBe Some(Move(1, 1))
    }

    "return None for malformed input" in {
      val field = fieldCreator.create(2, 2)
      val (view, _, _) = makeView("", field)

      view.parseInput("abcd") shouldBe None
      view.parseInput("1 x")  shouldBe None
      view.parseInput("1")    shouldBe None
      view.parseInput("1 2 3") shouldBe None
    }
  }
}
