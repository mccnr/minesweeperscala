package view

import org.scalatest.wordspec.AnyWordSpec
import controller.*
import htwg.minesweeperse.controller.GameController
import htwg.minesweeperse.model.{Cell, Field}
import htwg.minesweeperse.view.GameView

import java.io.*
import org.scalatest.matchers.should.Matchers.*

class ViewWSTest extends AnyWordSpec {

  "GameView" should {

    "process valid numeric input" in {

      val input = "1 1\n"
      val in = new BufferedReader(new InputStreamReader(
        new ByteArrayInputStream(input.getBytes())
      ))
      val output = new ByteArrayOutputStream()
      val out = new PrintStream(output)

      val controller = new GameController(Field(2, 2, Vector.fill(2, 2)(Cell(0))))
      val view = new GameView(controller, out, in)
      view.start()

      val text = output.toString
      assert(text.contains("Willkommen bei Minesweeper"))
      assert(controller.field.cells.flatten.exists(_.revealed)) // processMove wurde ausgeführt
      assert(!text.contains("Bitte zwei Zahlen eingeben, z. B. 2 3."))

    }

    "print error for invalid input" in {

      val input = "abc def\n"
      val in = new BufferedReader(new InputStreamReader(
        new ByteArrayInputStream(input.getBytes())
      ))
      val output = new ByteArrayOutputStream()
      val out = new PrintStream(output)

      val controller = new GameController(Field(2, 2, Vector.fill(2, 2)(Cell(0))))
      val view = new GameView(controller, out, in)
      view.start()

      val text = output.toString
      assert(text.contains("Willkommen bei Minesweeper"))
      assert(text.contains("Bitte zwei Zahlen eingeben, z. B. 2 3."))
      assert(!controller.field.cells.flatten.exists(_.revealed))

    }

    "update the view when controller notifies" in {
      val output = new ByteArrayOutputStream()
      val out = new PrintStream(output)

      val controller = new GameController(Field(2, 2, Vector.fill(2, 2)(Cell(0))))
      val view = new GameView(controller, out)

      controller.processMove(0, 0) // ruft notifyObservers()
      assert(output.toString.contains("|"))
    }

    "print OutOfBounds when move is outside field" in {

      val input = "9 9\n"
      val in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(input.getBytes())))
      val outStream = new ByteArrayOutputStream()
      val out = new PrintStream(outStream)

      val controller = new GameController(Field(2, 2, Vector.fill(2, 2)(Cell(0))))
      val view = new GameView(controller, out, in)
      view.start()

      outStream.toString should include("Koordinate ist außerhalb des Felds.")

    }

    "print Game Over when a mine is revealed" in {

      val cells = Vector(
        Vector(Cell(1, false), Cell(0, false)),
        Vector(Cell(0, false), Cell(0, false))
      )

      val input = "1 1\n"
      val in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(input.getBytes())))
      val outStream = new ByteArrayOutputStream()
      val out = new PrintStream(outStream)

      val controller = new GameController(Field(2, 2, cells))
      val view = new GameView(controller, out, in)
      view.start()

      outStream.toString should include("Game Over.")
    }

    "print Win when all non-mine cells are revealed" in {

      val cells = Vector(
        Vector(Cell(0, true), Cell(0, true)),
        Vector(Cell(0, true), Cell(0, false)) // letzter Schritt löst Win aus
      )

      val input = "2 2\n"
      val in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(input.getBytes())))
      val outStream = new ByteArrayOutputStream()
      val out = new PrintStream(outStream)

      val controller = new GameController(Field(2, 2, cells))
      val view = new GameView(controller, out, in)
      view.start()

      outStream.toString should include("Glückwunsch, du hast alle Minen gefunden!")

    }

    "print on successful Revealed result" in {

      val cells = Vector(
        Vector(Cell(1), Cell(0), Cell(0)),
        Vector(Cell(0), Cell(0), Cell(0)),
        Vector(Cell(0), Cell(0), Cell(0))
      )

      val input = "2 2\n\n"
      val in = new BufferedReader(new InputStreamReader(
        new ByteArrayInputStream(input.getBytes())
      ))
      val outStream = new ByteArrayOutputStream()
      val out = new PrintStream(outStream)

      val controller = new GameController(Field(3, 3, cells))
      val view = new GameView(controller, out, in)
      view.start()

      outStream.toString should include("Erfolgreich aufgedeckt.")

    }
  }
}
