/* package htwg.minesweeperse

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

import java.io._

class MainWSTest extends AnyWordSpec {

  "runMain" should {

    "start only TUI when in test mode" in {
      // GUI deaktivieren
      //sys.props("test.env") = "true"

      // TUI-Eingabe, sofort Beenden
      val input = new ByteArrayInputStream("\n".getBytes())
      System.setIn(input)

      // Ausgabe abfangen
      val output = new ByteArrayOutputStream()
      System.setOut(new PrintStream(output))

      // Hauptprogramm starten
      runMain()

      // kleine Pause, damit der TUI-Thread starten kann
      Thread.sleep(100)

      val text = output.toString

      // Erwartungen
      text should include ("Willkommen bei Minesweeper")
      text should include ("Gib eine valide Koordinate ein (Z S): ")
    }
  }
} */
