import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.must.Matchers.{be, noException}
import model._
import controller._
import view._
import java.io._

class MainWSTest extends AnyWordSpec {

  "runMain" should {

    "start the game when not in test mode" in {
      sys.props -= "test.env"

      // simuliert Eingabe
      val input = new ByteArrayInputStream("\n".getBytes())
      System.setIn(input)
      val output = new ByteArrayOutputStream() // fängt alles ab, was print schreibt, zum prüfen
      System.setOut(new PrintStream(output))


      // Spiel starten
      runMain()

      val text = output.toString
      assert(text.contains("Willkommen bei Minesweeper")) // Programmstart bestätigen
    }
  }
}