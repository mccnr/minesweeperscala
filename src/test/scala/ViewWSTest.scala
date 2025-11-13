package view

import org.scalatest.wordspec.AnyWordSpec
import model._
import controller._
import java.io._

class ViewWSTest extends AnyWordSpec {

  "GameView" should {

    "process valid numeric input" in {
      // Testmodus temporär ausschalten
      val hadProp = sys.props.contains("test.env")
      if (hadProp) sys.props -= "test.env"

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
      assert(!text.contains("Bitte zwei Zahlen eingeben"))

      // Testmodus wiederherstellen
      if (hadProp) System.setProperty("test.env", "true")
    }

    "print error for invalid input" in {
      val hadProp = sys.props.contains("test.env")
      if (hadProp) sys.props -= "test.env"

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
      assert(text.contains("Bitte zwei Zahlen eingeben"))
      assert(!controller.field.cells.flatten.exists(_.revealed))

      if (hadProp) System.setProperty("test.env", "true")
    }

    "update the view when controller notifies" in {
      val output = new ByteArrayOutputStream()
      val out = new PrintStream(output)

      val controller = new GameController(Field(2, 2, Vector.fill(2, 2)(Cell(0))))
      val view = new GameView(controller, out)

      controller.processMove(0, 0) // ruft notifyObservers()
      assert(output.toString.contains("|"))
    }
  }
}
