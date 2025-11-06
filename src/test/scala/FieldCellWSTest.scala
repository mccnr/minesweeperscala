import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class FieldCellWSTest extends AnyWordSpec {

  // Test für einzelne Zellen
  "A Cell" should {

    // Prüft ob isMine korrekt funktioniert
    "Check if isMine = true when value == 1" in {
      val c = Cell(1)
      c.isMine shouldBe true
    }

    "Check if isMine = false when value != 1" in {
      val c = Cell(0)
      c.isMine shouldBe false
    }

    // Anzeige einer Zelle abhängig vom Zustand
    "Display '?' for hidden cells" in {
      Cell(1).display() shouldBe "?"
      Cell(0).display() shouldBe "?"
    }

    "Display '*' for revealed mines" in {
      Cell(1, revealed = true).display() shouldBe "*"
    }

    "Display numbers or blank for revealed non-mines" in {
      val c = Cell(0, revealed = true)
      c.display(Some(0)) shouldBe " "
      c.display(Some(2)) shouldBe "2"
    }
  }

  // Tests für das Spielfeld
  "A Field" should {

    // Struktur für ein 1x1 Feld prüfen (verdeckt)
    "Display correct structure and symbols for 1x1 field" in {
      val cells = Vector(Vector(Cell(1)))
      val field = Field(1, 1, cells)
      val out = field.show()

      out should include("| ? |")
      out should startWith("-")
      out.trim should endWith("-")
    }

    // 1x2 Feld prüfen (verdeckt)
    "Display correct structure and symbols for 1x2 field" in {
      val cells = Vector(Vector(Cell(1), Cell(0)))
      val field = Field(1, 2, cells)
      val out = field.show()

      out should include("| ? ? |")
      out should startWith("-")
      out.trim should endWith("-")
    }

    // 2x1 Feld prüfen (verdeckt)
    "Display correct structure and symbols for 2x1 field" in {
      val cells = Vector(Vector(Cell(1)), Vector(Cell(0)))
      val field = Field(2, 1, cells)
      val out = field.show()

      out should include("| ? |")
      out should include("| ? |")
      out should startWith("-")
      out.trim should endWith("-")
    }

  }

  // Test für randomField
  "randomField" should {

    "Create a 1x1 field with valid cell value" in {
      val f = randomField(1, 1)
      f.rows shouldBe 1
      f.cols shouldBe 1
      f.cells.length shouldBe 1
      f.cells.foreach(_.length shouldBe 1)
      all(f.cells.flatten.map(_.value)) should (be(0) or be(1))
    }

    "Create a 1x2 field with valid cell values" in {
      val f = randomField(1, 2)
      f.rows shouldBe 1
      f.cols shouldBe 2
      f.cells.length shouldBe 1
      f.cells.foreach(_.length shouldBe 2)
      all(f.cells.flatten.map(_.value)) should (be(0) or be(1))
    }

    "Create a 2x1 field with valid cell values" in {
      val f = randomField(2, 1)
      f.rows shouldBe 2
      f.cols shouldBe 1
      f.cells.length shouldBe 2
      f.cells.foreach(_.length shouldBe 1)
      all(f.cells.flatten.map(_.value)) should (be(0) or be(1))
    }
  }

  // verhindert interaktiven Start während Tests
  "runMain" should {
    "Run main" in {
      System.setProperty("test.env", "true")
      runMain()
    }
  }
}
