package htwg.minesweeperse.view

import htwg.minesweeperse.controller._
import htwg.minesweeperse.model._
import htwg.minesweeperse.util.strategy.StandardRevealStrategy
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec
import java.io._

class ViewWSTest extends AnyWordSpec {

  // Hilfsfunktion für eine neue View und Controller pro Test
  private def makeView(input: String, field: Field = Field(2,2,Vector.fill(2)(Vector.fill(2)(Cell(0))))):
  (GameView, GameController, ByteArrayOutputStream) =

    val in  = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(input.getBytes())))
    val outBytes = new ByteArrayOutputStream()
    val out = new PrintStream(outBytes)

    val controller = new GameController(field, StandardRevealStrategy())
    val view = new GameView(controller, out, in)

    (view, controller, outBytes)

  "GameView" should {

    "process valid numeric input" in {
      val (view, controller, bytes) =
        makeView("1 1\n")

      view.startGameLoop()

      val text = bytes.toString
      text should include ("Willkommen bei Minesweeper")
      controller.field.cells.flatten.exists(_.revealed) shouldBe true
      text should not include "Bitte zwei Zahlen eingeben"
    }

    "print error for invalid input" in {
      val (view, controller, bytes) =
        makeView("abc def\n")

      view.startGameLoop()

      val text = bytes.toString
      text should include ("Bitte zwei Zahlen eingeben")
      controller.field.cells.flatten.exists(_.revealed) shouldBe false
    }

    "update view when controller notifies observers" in {
      val (view, controller, bytes) =
        makeView("") // Input irrelevant

      controller.processMove(0,0) // notifyObservers()

      bytes.toString should include ("|") // Feldanzeige
    }

    "print OutOfBounds when move is outside field" in {
      val (view, controller, bytes) =
        makeView("9 9\n")

      view.startGameLoop()

      bytes.toString should include ("Koordinate ist außerhalb des Felds.")
    }

    "print Game Over when a mine is revealed" in {
      val cells = Vector(
        Vector(Cell(1), Cell(0)),
        Vector(Cell(0), Cell(0))
      )
      val field = Field(2,2,cells)

      val (view, controller, bytes) =
        makeView("1 1\n", field)

      view.startGameLoop()

      bytes.toString should include ("Game Over.")
    }

    "print Win when all non-mine cells are revealed" in {
      val cells = Vector(
        Vector(Cell(0, true), Cell(0, true)),
        Vector(Cell(0, true), Cell(0, false))
      )
      val field = Field(2,2,cells)

      val (view, controller, bytes) =
        makeView("2 2\n", field)

      view.startGameLoop()

      bytes.toString should include ("Glückwunsch, du hast alle Minen gefunden!")
    }

    "print on successful Revealed result" in {
      val cells = Vector(
        Vector(Cell(1), Cell(0), Cell(0)),
        Vector(Cell(0), Cell(0), Cell(0)),
        Vector(Cell(0), Cell(0), Cell(0))
      )
      val field = Field(3,3,cells)

      val (view, controller, bytes) =
        makeView("2 2\n\n", field)

      view.startGameLoop()

      bytes.toString should include ("Erfolgreich aufgedeckt.")
    }

    "show welcome message" in {
      val (view, _, bytes) = makeView("")
      view.showWelcome()
      bytes.toString should include ("Willkommen bei Minesweeper")
    }

    "show field in showField()" in {
      val (view, _, bytes) = makeView("")
      view.showField()
      bytes.toString should include ("|")
    }

    "read input correctly in readInput()" in {
      val (view, _, _) = makeView("1 1")
      view.readInput() shouldBe "1 1"
    }

    "parse undo command" in {
      val (view, _, _) = makeView("")
      view.parseInput("undo") shouldBe Some(UndoCmd)
    }

    "parse redo command" in {
      val (view, _, _) = makeView("")
      view.parseInput("redo") shouldBe Some(RedoCmd)
    }

    "parse valid move command" in {
      val (view, _, _) = makeView("")
      view.parseInput("2 2") shouldBe Some(Move(1, 1))
    }

    "return None for malformed input" in {
      val (view, _, _) = makeView("")

      view.parseInput("abcd") shouldBe None
      view.parseInput("1 x") shouldBe None
      view.parseInput("1") shouldBe None
      view.parseInput("1 2 3") shouldBe None
    }
  }
}
