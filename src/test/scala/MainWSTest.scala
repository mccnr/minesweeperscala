import org.scalatest.wordspec.AnyWordSpec
import java.io.{ByteArrayInputStream, ByteArrayOutputStream, PrintStream}
import scala.io.BufferedSource

class MainWSTest extends AnyWordSpec {

  def emptyField(rows: Int, cols: Int): Field =
    Field(rows, cols, Vector.fill(rows, cols)(Cell(0, false)))

  "runGameInteractive" should {

    "Show that an wrong input has been made" in {
      val input = "abc def\n"
      val in = new java.io.BufferedReader(new java.io.InputStreamReader(
        new ByteArrayInputStream(input.getBytes())
      ))

      val output = new ByteArrayOutputStream()
      val out = new PrintStream(output)

      runGameInteractive(in, out, emptyField(6, 6))

      val text = output.toString
      assert(text.contains("Bitte zwei Zahlen eingeben, z. B. 2 3."))
    }

    "should reveal the correct cell when a valid input is given" in {
      val input = "1 1\n"
      val in = new java.io.BufferedReader(new java.io.InputStreamReader(
        new ByteArrayInputStream(input.getBytes())
      ))
      val output = new ByteArrayOutputStream()
      val out = new PrintStream(output)

      val initial = emptyField(6, 6) // kontrolliertes Spielfeld (keine Minen)
      val finalField = runGameInteractive(in, out, initial)

      assert(finalField.cells(0)(0).revealed == true) // jetzt testbar
    }
   }

  "reveal should return the same field if the cell is already revealed" in {
    val field = emptyField(3, 3)
    val fieldRevealedOnce = field.reveal(1, 1)
    val fieldRevealedTwice = fieldRevealedOnce.reveal(1, 1)

    assert(fieldRevealedOnce eq fieldRevealedTwice) // gleiche Referenz = kein neues Field
  }

  "reveal should reveal all mines when a mine is clicked" in {
    val mineField = Field(2, 2, Vector(
      Vector(Cell(1), Cell(0)),
      Vector(Cell(0), Cell(0))
    ))

    val result = mineField.reveal(0, 0)

    // Die Mine (0,0) muss nun revealed sein:
    assert(result.cells(0)(0).revealed == true)

    // Und es dürfen keine anderen Zellen revealed sein:
    assert(result.cells(0)(1).revealed == false)
    assert(result.cells(1)(0).revealed == false)
    assert(result.cells(1)(1).revealed == false)
  }

  "should print an out of bounds message when coordinates are invalid" in {
    val input = "7 7\n" // außerhalb des 6x6 Feldes
    val in = new java.io.BufferedReader(new java.io.InputStreamReader(
      new ByteArrayInputStream(input.getBytes())
    ))
    val output = new ByteArrayOutputStream() // landet in output, um zu testen
    val out = new PrintStream(output)

    val initial = emptyField(6, 6)
    runGameInteractive(in, out, initial)

    assert(output.toString.contains("Koordinate ist außerhalb des Felds."))
  }

  "should print Game Over when a mine is revealed" in {
    val mineField = Field(6, 6, Vector.tabulate(6, 6) { (r, c) =>
      if r == 0 && c == 0 then Cell(1, false) else Cell(0, false)
    })

    val input = "1 1\n" // klickt genau die Mine
    val in = new java.io.BufferedReader(new java.io.InputStreamReader(
      new ByteArrayInputStream(input.getBytes())
    ))
    val output = new ByteArrayOutputStream()
    val out = new PrintStream(output)

    runGameInteractive(in, out, mineField)

    val text = output.toString
    assert(text.contains("Game Over."))
  }

  "runMain should call runGameInteractive when not in test mode" in {
    sys.props -= "test.env" // Entfernt Test-Flag, d.h. runMain() startet Spiel

    val input = "1 1\n"
    System.setIn(new java.io.ByteArrayInputStream(input.getBytes()))
    val output = new java.io.ByteArrayOutputStream()
    System.setOut(new java.io.PrintStream(output))

    runMain()

    assert(output.toString.contains("Willkommen bei Minesweeper"))
  }
}