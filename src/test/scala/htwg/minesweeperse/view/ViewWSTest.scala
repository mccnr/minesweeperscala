package htwg.minesweeperse.view

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

import java.io.*

import htwg.minesweeperse.controllerComponent.impl.implGC
import htwg.minesweeperse.controllerComponent.impl.IController

import htwg.minesweeperse.model.cell.Cell
import htwg.minesweeperse.model.fieldComponent.impl.{IField, implFieldAdvanced}
import htwg.minesweeperse.model.fileIoComponent.IFileIO

import htwg.minesweeperse.util.command.{Move, RedoCmd, UndoCmd}
import htwg.minesweeperse.util.strategy.revealComponent.impl.StandardRevealStrategy

class ViewWSTest extends AnyWordSpec {
  
  private object DummyFileIO extends IFileIO {
    override def save(field: IField, seconds: Int): Unit = ()
    override def load(): (IField, Int) =
      (new implFieldAdvanced(1, 1, Vector(Vector(Cell(0)))), 0)
  }

  // Hilfsfunktionen
  def emptyCell(): Cell = Cell(0)
  def mineCell(): Cell  = Cell(1)

  def fieldFromCells(cells: Vector[Vector[Cell]]): IField =
    new implFieldAdvanced(cells.length, cells.head.length, cells)

  private def makeView(
    input: String,
    field: IField
    ): (GameView, IController, ByteArrayOutputStream) = {

    val in       = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(input.getBytes)))
    val outBytes = new ByteArrayOutputStream()
    val out      = new PrintStream(outBytes)

    val controller =
      new implGC(field, new StandardRevealStrategy, DummyFileIO)

    val view = new GameView(controller, out, in)

    (view, controller, outBytes)
  }

  // Tests
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

      controller.processMove(r, c)
      view.handleResult(controller.lastResult)

      controller.field.isRevealed(0, 0) shouldBe true
    }

    "print error for invalid input" in {
      val field = fieldFromCells(
        Vector(
          Vector(emptyCell(), emptyCell()),
          Vector(emptyCell(), emptyCell())
        )
      )

      val (view, _, bytes) = makeView("abc def\n", field)

      val raw = view.readInput()
      view.parseInput(raw) match
        case None => view.handleInvalidInput(raw)
        case _    => fail("Should be invalid")

      bytes.toString should include("Bitte zwei Zahlen eingeben")
    }

    "print OutOfBounds when move is outside field" in {
      val field = fieldFromCells(
        Vector(
          Vector(emptyCell(), emptyCell()),
          Vector(emptyCell(), emptyCell())
        )
      )

      val (view, controller, bytes) = makeView("9 9\n", field)

      val raw = view.readInput()
      val Some(Move(r, c)) = view.parseInput(raw)

      controller.processMove(r, c)
      view.handleResult(controller.lastResult)

      bytes.toString should include("Koordinate ist außerhalb des Felds.")
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

      bytes.toString should include("Game Over")
    }

    "print Win when all non-mine cells are revealed" in {
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

      bytes.toString should include("Glückwunsch")
    }

    "parse undo command" in {
      val field = fieldFromCells(Vector(Vector(emptyCell())))
      val (view, _, _) = makeView("", field)

      view.parseInput("undo") shouldBe Some(UndoCmd)
    }

    "parse redo command" in {
      val field = fieldFromCells(Vector(Vector(emptyCell())))
      val (view, _, _) = makeView("", field)

      view.parseInput("redo") shouldBe Some(RedoCmd)
    }

    "parse valid move command" in {
      val field = fieldFromCells(Vector(Vector(emptyCell(), emptyCell())))
      val (view, _, _) = makeView("", field)

      view.parseInput("2 1") shouldBe Some(Move(1, 0))
    }

    "return None for malformed input" in {
      val field = fieldFromCells(Vector(Vector(emptyCell())))
      val (view, _, _) = makeView("", field)

      view.parseInput("abcd")  shouldBe None
      view.parseInput("1 x")   shouldBe None
      view.parseInput("1")     shouldBe None
      view.parseInput("1 2 3") shouldBe None
    }
  }
}
