import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.must.Matchers.{be, noException}
import model._
import controller._
import view._
import java.io._

class MainWSTest extends AnyWordSpec {

  "runMain" should {

    "not start the game when in test mode" in {
      System.setProperty("test.env", "true")
      noException should be thrownBy runMain()
      System.clearProperty("test.env")
    }

    "start the game when not in test mode" in {
      sys.props -= "test.env"

      // künstliche Eingabe: nur Enter, beendet sofort
      val input = new ByteArrayInputStream("\n".getBytes())
      System.setIn(input)
      val output = new ByteArrayOutputStream()
      System.setOut(new PrintStream(output))


      // Spiel starten
      runMain()

      val text = output.toString
      assert(text.contains("Willkommen bei Minesweeper"))
    }
  }
}